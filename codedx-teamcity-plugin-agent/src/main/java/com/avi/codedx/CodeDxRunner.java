package com.avi.codedx;

import com.avi.codedx.common.CodeDxConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class CodeDxRunner implements AgentBuildRunner {

	@NotNull
	@Override
	public BuildProcess createBuildProcess(@NotNull AgentRunningBuild agentRunningBuild, @NotNull BuildRunnerContext buildRunnerContext) throws RunBuildException {
		Map<String, String> parameters = buildRunnerContext.getRunnerParameters();

		final String codeDxUrl = parameters.get(CodeDxConstants.SETTINGS_CODEDX_URL_KEY);
		final String apiToken = parameters.get(CodeDxConstants.SETTINGS_API_TOKEN_KEY);
		final String projectId = parameters.get(CodeDxConstants.SETTNGS_CODEDX_PROJECT_KEY);
		final String filesToUpload = parameters.get(CodeDxConstants.SETTINGS_FILES);
		final File workingDirectory = buildRunnerContext.getWorkingDirectory();

		return new CodeDxBuildProcessAdapter(codeDxUrl, apiToken, projectId, filesToUpload, workingDirectory);
	}

	@NotNull
	@Override
	public AgentBuildRunnerInfo getRunnerInfo() {
		return new CodeDxRunnerInfo();
	}
}