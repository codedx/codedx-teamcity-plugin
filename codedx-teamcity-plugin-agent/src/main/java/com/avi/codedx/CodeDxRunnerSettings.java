package com.avi.codedx;

import io.swagger.client.model.ProjectId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public CodeDxRunnerSettings(String url,
	                            String apiToken,
	                            String projectId,
	                            String filesToUpload,
	                            String filesToExclude,
	                            String severityToBreakBuild,
	                            String onlyFailOnNewFindings,
	                            String analysisName,
	                            String toolOutputFiles) {
		this.url = url;
		this.apiToken = apiToken;
		this.projectId = projectId;
		this.filesToUpload = filesToUpload;
		this.filesToExclude = filesToExclude;
		this.severityToBreakBuild = severityToBreakBuild;
		this.onlyFailOnNewFindings = onlyFailOnNewFindings;
		this.analysisName = analysisName;
		this.toolOutputFiles = toolOutputFiles;
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

	public List<File> getFilesToUpload(File workingDirectory) throws IOException {
		ArrayList<File> files = new ArrayList<>();
		File zip = Archiver.archive(workingDirectory, this.filesToUpload, this.filesToExclude, "SourceAndBinariesZip");
		files.add(zip);

		if (toolOutputFiles != null) {
			String[] toolOutputFileNames = this.toolOutputFiles.split(",");
			for (int i = 0; i < toolOutputFileNames.length; i++) {
				String filename = toolOutputFileNames[i].trim();
				files.add(new File(workingDirectory, filename));
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

	public String getAnalysisName() {
		return this.analysisName;
	}
}
