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

package com.codedx;

import com.codedx.common.security.TeamCityHostnameVerifierFactory;
import com.codedx.server.CodeDxCredentials;
import com.codedx.common.security.SSLSocketFactoryFactory;
import com.codedx.client.ApiClient;
import com.codedx.client.ApiException;
import com.codedx.client.api.ProjectsApi;
import com.codedx.client.model.Projects;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.net.ssl.SSLHandshakeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class CodeDxServer extends BaseController {
	private PluginDescriptor myDescriptor;
	protected final ObjectMapper mapper = new ObjectMapper();

	public CodeDxServer(WebControllerManager manager, PluginDescriptor descriptor) {
		manager.registerController("/codedx.html",this);
		myDescriptor=descriptor;
	}

	@Nullable
	@Override
	protected ModelAndView doHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		CodeDxCredentials credentials = mapper.readValue(httpServletRequest.getInputStream(), CodeDxCredentials.class);

		try {
			String codedxURL = credentials.getCodeDxUrl();
			String fingerprint = credentials.getFingerprint();
			URL url = new URL(codedxURL);
			String host = url.getHost();

			ApiClient apiClient = new ApiClient();
			apiClient.setBasePath(codedxURL);
			apiClient.setApiKey(credentials.getCodeDxApiToken());

			if (fingerprint != null && !fingerprint.isEmpty()) {
				apiClient.getHttpClient().setSslSocketFactory(SSLSocketFactoryFactory.getFactory(fingerprint));
				apiClient.getHttpClient().setHostnameVerifier(TeamCityHostnameVerifierFactory.getVerifier(host));
			}

			ProjectsApi projectsApi = new ProjectsApi();
			projectsApi.setApiClient(apiClient);

			Projects projects = projectsApi.getProjects();
			String projectsJson = mapper.writeValueAsString(projects);
			httpServletResponse.getOutputStream().print(projectsJson);
		} catch (IllegalArgumentException e){
			// Bad URL?
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			httpServletResponse.getOutputStream().print("Invalid URL");
		} catch (ApiException e) {
			// Bad API Token?
			int responseCode = e.getCode();
			String message;
			switch (responseCode) {
				case 403:
						message = "API token does not have permission to access Code Dx projects";
						break;
				case 404:
						message = "Unable to retrieve project list. Please check if URL is correct.";
						break;
				default:
						Throwable cause = e.getCause();
						if (cause != null) {
							message = getBetterErrorMessage(cause);
						} else {
							message = e.getMessage();
						}
						break;
			}
			responseCode = responseCode == 0 ? 500 : responseCode;
			httpServletResponse.setStatus(responseCode);
			httpServletResponse.getOutputStream().print(message);
		} catch (MalformedURLException e) {
			httpServletResponse.setStatus(404);
			httpServletResponse.getOutputStream().print("Invalid URL");
		}

		return null;
	}

	private static String getBetterErrorMessage(Throwable cause) {
		String betterMessage = cause.getMessage();

		if (cause instanceof ConnectException)
			betterMessage = "Connection failed, is the URL/port correct?";
		else if (cause instanceof SSLHandshakeException)
			betterMessage = "The SSL Certificate presented by the server is invalid. If this is expected, please input the SHA1 fingerprint in the advanced options";
		else if (cause instanceof UnknownHostException)
			betterMessage = "Unknown host";

		return betterMessage;
	}
}