package com.avi.codedx;

import io.swagger.client.model.ProjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class CodeDxRunnerSettings {
	private final String url;
	private final String apiToken;
	private final String projectId;
	private final String filesToUpload;
	private final String severityToBreakBuild;
	private final String onlyFailOnNewFindings;
	private final String analysisName;

	public CodeDxRunnerSettings(String url,
	                            String apiToken,
	                            String projectId,
	                            String filesToUpload,
	                            String severityToBreakBuild,
	                            String onlyFailOnNewFindings,
	                            String analysisName) {
		this.url = url;
		this.apiToken = apiToken;
		this.projectId = projectId;
		this.filesToUpload = filesToUpload;
		this.severityToBreakBuild = severityToBreakBuild;
		this.onlyFailOnNewFindings = onlyFailOnNewFindings;
		this.analysisName = analysisName;
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

	public List<File> getFilesToUpload(File workingDirectory) {
		ArrayList<File> files = new ArrayList<File>();
		String[] fileNames = this.filesToUpload.split(",");

		for(int i = 0; i < fileNames.length; i++) {
			String filename = fileNames[i].trim();
			files.add(new File(workingDirectory, filename));
		}

		return files;
	}

	public String getSeverityToBreakBuild(){
		return this.severityToBreakBuild;
	}

	public boolean onlyFailOnNewFindings() {
		return this.onlyFailOnNewFindings != null && this.onlyFailOnNewFindings.equals("true");
	}

	public String getAnalysisName() {
		return this.analysisName;
	}
}
