package com.avi.codedx.server;

import com.avi.codedx.common.CodeDxConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CodeDxRunTypePropertiesProcessor implements PropertiesProcessor {
	@Override
	public Collection<InvalidProperty> process(Map<String, String> properties) {
		List<InvalidProperty> result = new Vector<InvalidProperty>();

		final String files = properties.get(CodeDxConstants.SETTINGS_FILES);
		final String project = properties.get(CodeDxConstants.SETTINGS_PROJECT);


		return result;
	}
}
