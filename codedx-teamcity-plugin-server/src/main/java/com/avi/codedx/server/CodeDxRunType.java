package com.avi.codedx.server;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import com.avi.codedx.common.CodeDxConstants;
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
