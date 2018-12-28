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

package com.codedx.server;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import com.codedx.common.CodeDxConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CodeDxRunType extends RunType {
	private final PluginDescriptor myPluginDescriptor;

	public CodeDxRunType(final RunTypeRegistry runTypeRegistry, final PluginDescriptor pluginDescriptor) {
		myPluginDescriptor = pluginDescriptor;
		runTypeRegistry.registerRunType(this);
	}

	@NotNull
	@Override
	public String getType() {
		return CodeDxConstants.RUNNER_TYPE;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return CodeDxConstants.RUNNER_DISPLAY_NAME;
	}

	@NotNull
	@Override
	public String getDescription() {
		return CodeDxConstants.RUNNER_DESCRIPTION;
	}

	@Nullable
	@Override
	public PropertiesProcessor getRunnerPropertiesProcessor() {
		return new CodeDxRunTypePropertiesProcessor();
	}

	@Nullable
	@Override
	public String getEditRunnerParamsJspFilePath() {
		return myPluginDescriptor.getPluginResourcesPath("editCodeDxParams.jsp");
	}

	@Nullable
	@Override
	public String getViewRunnerParamsJspFilePath() {
		return null;
	}

	@Nullable
	@Override
	public Map<String, String> getDefaultRunnerProperties() {
		return new HashMap<String, String>();
	}
}
