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

import com.codedx.common.CodeDxConstants;
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
