/*
 * Copyright (c) 2018. Code Dx, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codedx.agent;

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
