package com.avi.codedx;

import com.avi.codedx.server.CodeDxCredentials;
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

public class AppServer extends BaseController {
	private PluginDescriptor myDescriptor;
	protected final ObjectMapper mapper = new ObjectMapper();

	public AppServer (WebControllerManager manager, PluginDescriptor descriptor) {
		manager.registerController("/codedx.html",this);
		myDescriptor=descriptor;
	}

	@Nullable
	@Override
	protected ModelAndView doHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		CodeDxCredentials credentials = mapper.readValue(httpServletRequest.getInputStream(), CodeDxCredentials.class);

		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(credentials.getCodeDxUrl());
		apiClient.setApiKey(credentials.getCodeDxApiToken());

		ProjectsApi projectsApi = new ProjectsApi();
		projectsApi.setApiClient(apiClient);

		try {
			Projects projects = projectsApi.getProjects();
			String projectsJson = mapper.writeValueAsString(projects);
			httpServletResponse.getOutputStream().print(projectsJson);
		} catch (IllegalArgumentException e){
			// Bad URL?
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			httpServletResponse.getOutputStream().print(e.getMessage());
		} catch (ApiException e) {
			// Bad API Token?
			httpServletResponse.setStatus(e.getCode());
			httpServletResponse.getOutputStream().print(e.getMessage());
		}

		return null;
	}
}