package com.avi.codedx.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class CodeDxRunner implements AgentBuildRunner {

	@NotNull
	@Override
	public BuildProcess createBuildProcess(@NotNull AgentRunningBuild agentRunningBuild, @NotNull BuildRunnerContext buildRunnerContext) throws RunBuildException {
		try {
			Map<String, String> parameters = buildRunnerContext.getRunnerParameters();

			CodeDxRunnerSettings settings = new CodeDxRunnerSettings(parameters);

			return new CodeDxBuildProcessAdapter(settings, buildRunnerContext);
		} catch (MalformedURLException | GeneralSecurityException e) {
			throw new RunBuildException(e.getMessage(), e);
		}
	}

	@NotNull
	@Override
	public AgentBuildRunnerInfo getRunnerInfo() {
		return new CodeDxRunnerInfo();
	}
}
