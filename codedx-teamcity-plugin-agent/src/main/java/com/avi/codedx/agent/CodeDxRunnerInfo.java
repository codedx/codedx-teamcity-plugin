package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import org.jetbrains.annotations.NotNull;

public class CodeDxRunnerInfo implements AgentBuildRunnerInfo {
	@NotNull
	@Override
	public String getType() {
		return CodeDxConstants.RUNNER_TYPE;
	}

	@Override
	public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
		return true;
	}
}
