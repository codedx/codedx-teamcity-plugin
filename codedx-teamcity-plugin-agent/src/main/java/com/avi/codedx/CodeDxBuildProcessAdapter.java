package com.avi.codedx;

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
import java.util.Iterator;
import java.util.List;

public class CodeDxBuildProcessAdapter extends BuildProcessAdapter {
	private static final Logger LOG = Logger.getInstance(CodeDxBuildProcessAdapter.class.getName());
	private final BuildProgressLogger BUILD_PROGRESS_LOGGER;

	private volatile boolean hasFinished;
	private volatile BuildFinishedStatus statusCode;
	private volatile boolean isInterrupted;

	private final CodeDxRunnerSettings settings;
	private final List<File> filesToUpload;

	private final AnalysisApi analysisApi;

	public CodeDxBuildProcessAdapter(CodeDxRunnerSettings settings, BuildRunnerContext context) {
		this.settings = settings;
		this.filesToUpload = this.settings.getFilesToUpload(context.getWorkingDirectory());
		this.BUILD_PROGRESS_LOGGER = context.getBuild().getBuildLogger();

		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(this.settings.getUrl());
		apiClient.setApiKey(this.settings.getApiToken());

		this.analysisApi = new AnalysisApi();
		this.analysisApi.setApiClient(apiClient);
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

			String analysisPrepId = this.uploadFiles();

			// Make sure Code Dx can run the analysis before attempting to run it
			while(!readyToRunAnalysis) {
				Thread.sleep(1000);

				AnalysisQueryResponse response = this.analysisApi.queryAnalysisPrepState(analysisPrepId);
				List<String> inputIds = response.getInputIds();
				List<String> verificationErrors = response.getVerificationErrors();

				if(inputIds.size() == this.filesToUpload.size() && verificationErrors.isEmpty()) {
					readyToRunAnalysis = true;
				} else if (inputIds.size() == this.filesToUpload.size() && !verificationErrors.isEmpty()) {
					String errorMessage = this.getVerificationErrorMessage(verificationErrors);
					BUILD_PROGRESS_LOGGER.error(errorMessage);
					return BuildFinishedStatus.FINISHED_FAILED;
				}
			}

			String jobId = this.runAnalysis(analysisPrepId);
			return this.getAnalysisResults(jobId);
		} catch (Exception e) {
			BUILD_PROGRESS_LOGGER.error("An error occurred while attempting to run an analysis: " + e.getMessage());
			LOG.warnAndDebugDetails("An error occurred while attempting to run an analysis: ", e);
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

	@NotNull
	private String getVerificationErrorMessage(List<String> verificationErrors) {
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append("Code Dx reported verification errors for attempted analysis: \n");

		for(Iterator<String> i = verificationErrors.iterator(); i.hasNext();) {
			errorMessage.append(i).append("\n");
		}
		return errorMessage.toString();
	}

	private String uploadFiles() throws ApiException, IOException {
		ProjectId project = this.settings.getProjectId();

		AnalysisPrepResponse analysisPrep = this.analysisApi.createAnalysisPrep(project);
		String analysisPrepId = analysisPrep.getPrepId();

		for (Iterator<File> i = this.filesToUpload.iterator(); i.hasNext();) {
			File file = i.next();
			BUILD_PROGRESS_LOGGER.message("Uploading file: " + file.getCanonicalPath());
			analysisApi.uploadFile(analysisPrepId, file, null);
		}

		return analysisPrepId;
	}

	private String runAnalysis(String analysisPrepId) throws ApiException, InterruptedException {
		BUILD_PROGRESS_LOGGER.message("Running Code Dx analysis");
		Analysis analysis = analysisApi.runPreparedAnalysis(analysisPrepId);
		return analysis.getJobId();
	}

	private BuildFinishedStatus getAnalysisResults(String jobId) throws ApiException, InterruptedException {
		String severityToBreakBuild = this.settings.getSeverityToBreakBuild();

		if (severityToBreakBuild.equals("None")) {
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

			if (status == JobStatus.COMPLETED) {
				isAnalysisFinished = true;
			} else if(status == JobStatus.FAILED) {
				BUILD_PROGRESS_LOGGER.error("The Code Dx analysis has reported a failure");
				return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
			}
		}

		FindingDataApi findingDataApi = new FindingDataApi();
		findingDataApi.setApiClient(this.analysisApi.getApiClient());

		int projectId = this.settings.getProjectId().getProjectId();
		Query query = new Query();
		Sort sort = new Sort();
		Pagination pagination = new Pagination();

		Filter filter = new Filter();
		if (settings.onlyFailOnNewFindings()) {
			filter.put("status", "new");
		}
		filter.put("severity", severityToBreakBuild);

		query.setFilter(filter);
		query.setPagination(pagination);
		query.setSort(sort);

		Count count = findingDataApi.getFindingsCount(projectId, query);

		if (count.getCount() > 0){
			BUILD_PROGRESS_LOGGER.warning("Code Dx has reported " + count.getCount() + " findings with a severity level of " + severityToBreakBuild);
			return BuildFinishedStatus.FINISHED_FAILED;
		} else {
			return BuildFinishedStatus.FINISHED_SUCCESS;
		}
	}
}
