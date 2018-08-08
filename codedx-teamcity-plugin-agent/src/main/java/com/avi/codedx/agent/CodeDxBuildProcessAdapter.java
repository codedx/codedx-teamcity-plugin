package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import com.avi.codedx.common.security.SSLSocketFactoryFactory;
import com.avi.codedx.common.security.TeamCityHostnameVerifierFactory;
import com.intellij.openapi.diagnostic.Logger;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AnalysisApi;
import io.swagger.client.api.FindingDataApi;
import io.swagger.client.api.JobsApi;
import io.swagger.client.model.*;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcessAdapter;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeDxBuildProcessAdapter extends BuildProcessAdapter {
	private static final Logger LOG = Logger.getInstance(CodeDxBuildProcessAdapter.class.getName());
	private final BuildProgressLogger BUILD_PROGRESS_LOGGER;

	private volatile boolean hasFinished;
	private volatile BuildFinishedStatus statusCode;
	private volatile boolean isInterrupted;

	private final CodeDxRunnerSettings settings;
	private final BuildRunnerContext context;

	private final AnalysisApi analysisApi;
	private final FindingDataApi findingDataApi;

	private CodeDxBuildStatistics statsBeforeAnalysis;
	private CodeDxBuildStatistics statsAfterAnalysis;

	private final GroupedCountsRequest requestForSeverityGroupCount;
	private final GroupedCountsRequest requestForStatusGroupCount;

	private static final Map<Integer, String> severities;
	static {
		severities = new HashMap<>();
		severities.put(1, CodeDxConstants.INFO);
		severities.put(2, CodeDxConstants.LOW);
		severities.put(3, CodeDxConstants.MEDIUM);
		severities.put(4, CodeDxConstants.HIGH);
		severities.put(5, CodeDxConstants.CRITICAL);
	}


	public CodeDxBuildProcessAdapter(CodeDxRunnerSettings settings, BuildRunnerContext context) throws GeneralSecurityException, MalformedURLException {
		this.settings = settings;
		this.BUILD_PROGRESS_LOGGER = context.getBuild().getBuildLogger();
		this.context = context;
		String fingerprint = this.settings.getFingerprint();

		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(this.settings.getUrl());
		apiClient.setApiKey(this.settings.getApiToken());

		if (fingerprint != null && !fingerprint.isEmpty()) {
			apiClient.getHttpClient().setSslSocketFactory(SSLSocketFactoryFactory.getFactory(this.settings.getFingerprint()));
			apiClient.getHttpClient().setHostnameVerifier(TeamCityHostnameVerifierFactory.getVerifier(this.settings.getHostname()));
		}

		this.analysisApi = new AnalysisApi();
		this.analysisApi.setApiClient(apiClient);

		this.findingDataApi = new FindingDataApi();
		this.findingDataApi.setApiClient(apiClient);

		Filter filter = new Filter();
		filter.put("~status", "gone");

		this.requestForSeverityGroupCount = new GroupedCountsRequest();
		this.requestForSeverityGroupCount.setFilter(filter);
		this.requestForSeverityGroupCount.setCountBy("severity");

		this.requestForStatusGroupCount = new GroupedCountsRequest();
		this.requestForStatusGroupCount.setFilter(filter);
		this.requestForStatusGroupCount.setCountBy("status");
	}

	@Override
	public void interrupt(){
		this.isInterrupted = true;
	}

	@Override
	public boolean isFinished(){
		return hasFinished;
	}

	protected BuildFinishedStatus runProcess() {
		try{
			boolean readyToRunAnalysis = false;
			List<File> filesToUpload = this.settings.getFilesToUpload(context.getWorkingDirectory(), BUILD_PROGRESS_LOGGER);

			this.statsBeforeAnalysis = getBuildStats();

			String analysisPrepId = this.uploadFiles(filesToUpload);

			// Make sure Code Dx can run the analysis before attempting to run it
			while(!readyToRunAnalysis) {
				Thread.sleep(1000);

				AnalysisQueryResponse response = this.analysisApi.queryAnalysisPrepState(analysisPrepId);
				List<String> inputIds = response.getInputIds();
				List<String> verificationErrors = response.getVerificationErrors();

				if(inputIds.size() == filesToUpload.size() && verificationErrors.isEmpty()) {
					readyToRunAnalysis = true;
				} else if (inputIds.size() == filesToUpload.size() && !verificationErrors.isEmpty()) {
					String errorMessage = this.getVerificationErrorMessage(verificationErrors);
					BUILD_PROGRESS_LOGGER.error(CodeDxConstants.VERIFICATION_ERROR_MESSAGE);
					BUILD_PROGRESS_LOGGER.error(errorMessage);
					return BuildFinishedStatus.FINISHED_FAILED;
				}
			}

			String jobId = this.runAnalysis(analysisPrepId);
			return this.getAnalysisResults(jobId);
		} catch (ApiException e) {
			BUILD_PROGRESS_LOGGER.error(CodeDxConstants.API_ERROR_MESSAGE);
			BUILD_PROGRESS_LOGGER.error(e.getMessage());
			LOG.error(CodeDxConstants.API_ERROR_MESSAGE, e);
			return BuildFinishedStatus.FINISHED_FAILED;
		} catch (IOException e) {
			BUILD_PROGRESS_LOGGER.error(CodeDxConstants.IO_ERROR_MESSAGE);
			BUILD_PROGRESS_LOGGER.error(e.getMessage());
			LOG.error(CodeDxConstants.IO_ERROR_MESSAGE, e);
			return BuildFinishedStatus.FINISHED_FAILED;
		} catch (InterruptedException e) {
			BUILD_PROGRESS_LOGGER.error(CodeDxConstants.INTERRUPT_ERROR_MESSAGE);
			BUILD_PROGRESS_LOGGER.error(e.getMessage());
			LOG.error(CodeDxConstants.INTERRUPT_ERROR_MESSAGE, e);
			return BuildFinishedStatus.FINISHED_FAILED;
		}
	}

	@Override
	public void start() throws RunBuildException {
		try {
			this.statusCode = runProcess();
			hasFinished = true;
		} catch (Exception e) {
			hasFinished = false;
		}
	}

	@Override
	public BuildFinishedStatus waitFor() throws RunBuildException {
		while(!isInterrupted && !hasFinished) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RunBuildException(e);
			}
		}
		return hasFinished ? statusCode : BuildFinishedStatus.INTERRUPTED;
	}

	/**
	 * Returns a formatted error message to display to the user.
	 * @param verificationErrors
	 * @return
	 */
	@NotNull
	private String getVerificationErrorMessage(List<String> verificationErrors) {
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append("Code Dx reported verification errors for attempted analysis: \n");

		for(String error : verificationErrors) {
			errorMessage.append(error).append("\n");
		}
		return errorMessage.toString();
	}

	/**
	 * Upload files to the analysis prep one by one.
	 * @param files
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	private String uploadFiles(List<File> files) throws ApiException, IOException {
		ProjectId project = this.settings.getProjectId();

		AnalysisPrepResponse analysisPrep = this.analysisApi.createAnalysisPrep(project);
		String analysisPrepId = analysisPrep.getPrepId();

		for (File file : files) {
			String filename = file.getCanonicalPath();
			if (file.exists()) {
				BUILD_PROGRESS_LOGGER.message("Uploading file: " + filename);
				analysisApi.uploadFile(analysisPrepId, file, null);
			}
		}

		return analysisPrepId;
	}

	/**
	 * Trigger an analysis. If the user supplied an analysis name, set the analysis name.
	 * @param analysisPrepId
	 * @return
	 * @throws ApiException
	 * @throws InterruptedException
	 */
	private String runAnalysis(String analysisPrepId) throws ApiException, InterruptedException {
		BUILD_PROGRESS_LOGGER.message("Running Code Dx analysis");
		Analysis analysis = analysisApi.runPreparedAnalysis(analysisPrepId);
		String analysisName = this.settings.getAnalysisName();

		if (analysisName != null && !analysisName.isEmpty()) {
			int projectId = this.settings.getProjectId().getProjectId();
			int analysisId = analysis.getAnalysisId();
			AnalysisName an = new AnalysisName();
			an.setName(this.settings.getAnalysisName());

			analysisApi.setAnalysisName(projectId, analysisId, an);
		}

		return analysis.getJobId();
	}

	/**
	 * Wait for and process analysis results.
	 * @param jobId
	 * @return
	 * @throws ApiException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private BuildFinishedStatus getAnalysisResults(String jobId) throws ApiException, InterruptedException, IOException {
		boolean waitForAnalysisResults = this.settings.waitForResults();

		if (!waitForAnalysisResults) {
			return BuildFinishedStatus.FINISHED_SUCCESS;
		}

		BUILD_PROGRESS_LOGGER.message("Waiting for Code Dx analysis results");

		boolean isAnalysisFinished = false;
		JobsApi jobsApi = new JobsApi();
		jobsApi.setApiClient(this.analysisApi.getApiClient());

		while(!isAnalysisFinished) {
			Thread.sleep(5000);

			Job job = jobsApi.getJobStatus(jobId);
			JobStatus status = job.getStatus();

			BUILD_PROGRESS_LOGGER.progressMessage("Code Dx analysis status: " + status.toString());

			if (status == JobStatus.COMPLETED) {
				isAnalysisFinished = true;
			} else if (status != JobStatus.RUNNING && status != JobStatus.QUEUED) {
				switch (status) {
					case FAILED:
						BUILD_PROGRESS_LOGGER.error("The Code Dx analysis has reported a failure");
						break;
					case CANCELLED:
						BUILD_PROGRESS_LOGGER.error("The Code Dx analysis was cancelled");
						break;
				}
				return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
			}
		}

		this.statsAfterAnalysis = getBuildStats();

		// If user provides an archive name, create a zip containing an simple html file
		String archiveName = this.settings.getReportArchiveName();
		if(archiveName != null && !archiveName.isEmpty()) {
			CodeDxReportWriter reportWriter = new CodeDxReportWriter(archiveName,
				this.statsBeforeAnalysis,
				this.statsAfterAnalysis,
				this.settings.getUrl(),
				this.settings.getProjectId().getProjectId());
			reportWriter.writeReport(context.getWorkingDirectory());
		}

		if (failThisBuild()){
			BUILD_PROGRESS_LOGGER.warning("Code Dx has reported findings that match the configured build-failure value.");
			return BuildFinishedStatus.FINISHED_FAILED;
		} else {
			return BuildFinishedStatus.FINISHED_SUCCESS;
		}
	}

	/**
	 * Fail the build if a severity that matches user supplied criteria is reported.
	 * @return
	 * @throws ApiException
	 */
	private boolean failThisBuild() throws ApiException {
		Integer severityValueToBreakBuild = Integer.parseInt(this.settings.getSeverityToBreakBuild());

		if (severityValueToBreakBuild == 0) {
			return false;
		}

		List<GroupedCount> groupedCounts;
		if (this.settings.onlyFailOnNewFindings()) {
			groupedCounts = getNewFindingsGroupedBySeverity();
		} else {
			groupedCounts = this.statsAfterAnalysis.getGroupedSeverityCounts();
		}

		for(int i = severityValueToBreakBuild; i < 6; i++) {
			String severity = severities.get(i);
			int numberOfFindingsForSeverity = CodeDxBuildStatistics.getNumberOfFindingsForGroupAndName(groupedCounts, severity);
			if (numberOfFindingsForSeverity > 0) {
				return true;
			}
		}

		return false;
	}

	private List<GroupedCount> getNewFindingsGroupedBySeverity() throws ApiException {
		GroupedCountsRequest request = new GroupedCountsRequest();
		Filter filter = new Filter();

		filter.put("status", "new");
		request.setCountBy("severity");
		request.setFilter(filter);

		List<GroupedCount> newFindingsGrouped = this.findingDataApi.getFindingsGroupCount(this.settings.getProjectId().getProjectId(), request);
		return newFindingsGrouped;
	}

	private CodeDxBuildStatistics getBuildStats() throws ApiException {
		int projectId = this.settings.getProjectId().getProjectId();

		List<GroupedCount> groupedSeverityCounts = this.findingDataApi.getFindingsGroupCount(projectId, this.requestForSeverityGroupCount);
		List<GroupedCount> groupedStatusCounts = this.findingDataApi.getFindingsGroupCount(projectId, this.requestForStatusGroupCount);

		return new CodeDxBuildStatistics(groupedSeverityCounts, groupedStatusCounts);
	}
}