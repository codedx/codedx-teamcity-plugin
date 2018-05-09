package com.avi.codedx;

import com.avi.codedx.common.security.TeamCityHostnameVerifierFactory;
import com.avi.codedx.server.CodeDxCredentials;
import com.avi.codedx.common.security.SSLSocketFactoryFactory;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ProjectsApi;
import io.swagger.client.model.Projects;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;

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
			URL url = new URL(codedxURL);
			String host = url.getHost();

			ApiClient apiClient = new ApiClient();
			apiClient.setBasePath(credentials.getCodeDxUrl());
			apiClient.setApiKey(credentials.getCodeDxApiToken());
			apiClient.getHttpClient().setSslSocketFactory(SSLSocketFactoryFactory.getFactory(credentials.getFingerprint()));
			apiClient.getHttpClient().setHostnameVerifier(TeamCityHostnameVerifierFactory.getVerifier(host));

			ProjectsApi projectsApi = new ProjectsApi();
			projectsApi.setApiClient(apiClient);

			Projects projects = projectsApi.getProjects();
			String projectsJson = mapper.writeValueAsString(projects);
			httpServletResponse.getOutputStream().print(projectsJson);
		} catch (IllegalArgumentException e){
			// Bad URL?
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			httpServletResponse.getOutputStream().print("The URL supplied is invalid");
		} catch (ApiException e) {
			// Bad API Token?
			int responseCode = e.getCode();
			String message;
			switch (responseCode) {
				case 403:
						message = "API token does not have permission to access Code Dx projects";
						break;
				case 404:
						message = "URL not found, please enter a different URL";
						break;
				default:
						message = e.getMessage();
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
}