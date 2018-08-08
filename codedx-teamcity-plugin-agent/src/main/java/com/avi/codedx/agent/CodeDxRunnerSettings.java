package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import io.swagger.client.model.ProjectId;
import jetbrains.buildServer.agent.BuildProgressLogger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CodeDxRunnerSettings {
	private final String url;
	private final String apiToken;
	private final String projectId;
	private final String filesToUpload;
	private final String filesToExclude;
	private final String severityToBreakBuild;
	private final String onlyFailOnNewFindings;
	private final String analysisName;
	private final String toolOutputFiles;
	private final String fingerprint;
	private final String waitForResults;
	private final String reportArchiveName;

	public CodeDxRunnerSettings(Map<String, String> parameters) {
		this.url = parameters.get(CodeDxConstants.SETTINGS_CODEDX_URL_KEY);
		this.apiToken = parameters.get(CodeDxConstants.SETTINGS_API_TOKEN_KEY);
		this.projectId = parameters.get(CodeDxConstants.SETTINGS_CODEDX_PROJECT_KEY);
		this.filesToUpload = parameters.get(CodeDxConstants.SETTINGS_FILES);
		this.severityToBreakBuild = parameters.get(CodeDxConstants.SETTINGS_CODEDX_SEVERITY_KEY);
		this.onlyFailOnNewFindings = parameters.get(CodeDxConstants.SETTINGS_ONLY_NEW_FINDINGS_KEY);
		this.analysisName = parameters.get(CodeDxConstants.SETTINGS_ANALYSIS_NAME_KEY);
		this.toolOutputFiles = parameters.get(CodeDxConstants.SETTINGS_TOOL_OUTPUT_FILES_KEY);
		this.filesToExclude = parameters.get(CodeDxConstants.SETTINGS_FILES_EXCLUDED_KEY);
		this.fingerprint = parameters.get(CodeDxConstants.SETTINGS_SHA1_FINGERPRINT_KEY);
		this.waitForResults = parameters.get(CodeDxConstants.SETTINGS_WAIT_FOR_RESULTS_KEY);
		this.reportArchiveName = parameters.get(CodeDxConstants.SETTINGS_REPORT_ARCHIVE_NAME_KEY);
	}

	public String getUrl() {
		return this.url;
	}

	public String getApiToken() {
		return this.apiToken;
	}

	public ProjectId getProjectId() {
		ProjectId project = new ProjectId();
		project.setProjectId(Integer.parseInt(projectId));
		return project;
	}

	/***
	 * Returns a list of files relative to the working directory
	 * @param workingDirectory
	 * @return
	 * @throws IOException
	 */
	public List<File> getFilesToUpload(File workingDirectory, BuildProgressLogger logger) throws IOException {
		ArrayList<File> files = new ArrayList<>();
		File zip = Archiver.archive(workingDirectory, this.filesToUpload, this.filesToExclude, "SourceAndBinariesZip");
		files.add(zip);

		if (toolOutputFiles != null) {
			String[] toolOutputFileNames = this.toolOutputFiles.split(",");
			for (String fileName : toolOutputFileNames) {
				File file;
				fileName = fileName.trim();
				Path path = Paths.get(fileName);
				file = path.isAbsolute() ? path.toFile() : new File(workingDirectory, fileName);
				if (file.exists()) {
					files.add(file);
				} else {
					logger.warning("File: " + file.getCanonicalPath() + " does not exist. Skipping...");
				}
			}
		}

		return files;
	}

	public String getSeverityToBreakBuild(){
		return this.severityToBreakBuild;
	}

	public boolean onlyFailOnNewFindings() {
		return this.onlyFailOnNewFindings != null && this.onlyFailOnNewFindings.equals("true");
	}

	public boolean waitForResults() {
		return this.waitForResults != null && this.waitForResults.equals("true");
	}

	public String getAnalysisName() {
		return this.analysisName;
	}

	public String getFingerprint() {
		return this.fingerprint;
	}

	public String getHostname() throws MalformedURLException{
		URL fullUrl = new URL(this.url);
		return fullUrl.getHost();
	}

	public String getReportArchiveName() {
		return this.reportArchiveName;
	}
}
