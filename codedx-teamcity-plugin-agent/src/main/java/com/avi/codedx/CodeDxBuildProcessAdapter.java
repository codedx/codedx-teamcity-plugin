package com.avi.codedx;

import com.intellij.openapi.diagnostic.Logger;
import io.swagger.client.ApiClient;
import io.swagger.client.api.AnalysisApi;
import io.swagger.client.model.AnalysisPrepResponse;
import io.swagger.client.model.AnalysisQueryResponse;
import io.swagger.client.model.ProjectId;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcessAdapter;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeDxBuildProcessAdapter extends BuildProcessAdapter {
	private static final Logger LOG = Logger.getInstance(CodeDxBuildProcessAdapter.class.getName());
	private final BuildProgressLogger BUILD_PROGRESS_LOGGER;

	private volatile boolean hasFinished;
	private volatile BuildFinishedStatus statusCode;
	private volatile boolean isInterrupted;

	private final String url;
	private final String apiToken;
	private final String projectId;
	private final String filesToUpload;
	private final File workingDirectory;

	private final AnalysisApi analysisApi;

	public CodeDxBuildProcessAdapter(String url, String apiToken, String projectId, String filesToUpload, BuildRunnerContext context) {
		this.url = url;
		this.apiToken = apiToken;
		this.projectId = projectId;
		this.filesToUpload = filesToUpload;
		this.workingDirectory = context.getWorkingDirectory();
		this.BUILD_PROGRESS_LOGGER = context.getBuild().getBuildLogger();

		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(this.url);
		apiClient.setApiKey(apiToken);

		analysisApi = new AnalysisApi();
		analysisApi.setApiClient(apiClient);
	}

	public String getUrl() {
		return this.url;
	}

	public String getApiToken() {
		return this.apiToken;
	}

	public String getProjectId() {
		return this.projectId;
	}

	/*
	Files are comma separated
	 */
	public String getFilesToUpload() {
		return this.filesToUpload;
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
			boolean notReadyToRunAnalysis = true;
			ProjectId project = new ProjectId();
			project.setProjectId(Integer.parseInt(projectId));

			List<File> files = this.getFiles();

			AnalysisPrepResponse analysisPrep = this.analysisApi.createAnalysisPrep(project);
			String analysisPrepId = analysisPrep.getPrepId();

			for (Iterator<File> i = files.iterator(); i.hasNext();) {
				File file = i.next();
				analysisApi.uploadFile(analysisPrepId, file, null);
			}

			// Make sure Code Dx can run the analysis before attempting to run it
			while(notReadyToRunAnalysis) {
				AnalysisQueryResponse response = this.analysisApi.queryAnalysisPrepState(analysisPrepId);
				List<String> inputIds = response.getInputIds();
				List<String> verificationErrors = response.getVerificationErrors();

				if(inputIds.size() == files.size() && verificationErrors.isEmpty()) {
					notReadyToRunAnalysis = false;
				} else if (inputIds.size() == files.size() && !verificationErrors.isEmpty()) {
					String errorMessage = this.getVerificationErrorMessage(verificationErrors);
					BUILD_PROGRESS_LOGGER.error(errorMessage);
					return BuildFinishedStatus.FINISHED_FAILED;
				}
			}

			analysisApi.runPreparedAnalysis(analysisPrepId);
		} catch (Exception e) {
			BUILD_PROGRESS_LOGGER.error("Error uploading files to Code Dx: " + e.getMessage());
			LOG.warnAndDebugDetails("Error uploading files to Code Dx", e);
			return BuildFinishedStatus.FINISHED_FAILED;
		}
		return BuildFinishedStatus.FINISHED_SUCCESS;
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

	private List<File> getFiles(){
		ArrayList<File> files = new ArrayList<File>();
		String[] fileNames = this.filesToUpload.split(",");

		for(int i = 0; i < fileNames.length; i++) {
			String filename = fileNames[i].trim();
			files.add(new File(workingDirectory, filename));
		}

		return files;
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
}
