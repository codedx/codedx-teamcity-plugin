<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="com.avi.codedx.server.CodeDxConstantsBean"/>

<l:settingsGroup title="Code Dx Configuration">
	<tr>
		<th>
			<label>Code Dx URL:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.codeDxUrlKey}" className="longField"></props:textProperty>
				<span class="smallNote">The Code Dx URL where the files will be sent for analysis</span>
				<span class="error" id="error_${constants.codeDxUrlKey}"></span>
			</span>
		<td>
	</tr>
	<tr>
		<th>
			<label>API Token:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.codeDxAPITokenKey}" className="longField"></props:textProperty>
				<span class="error" id="error_${constants.codeDxAPITokenKey}"></span>
			</span>
			<span>
				<button type="button" onclick="getCodeDxProjects()">Verify</button>
				<span class="error" id="${constants.serverValidationErrorKey}"></span>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Project:</label>
		</th>
		<td>
			<span>
			<div id="selected-project-id" class="hidden">
				<props:displayValue name="${constants.codeDxProjectKey}"/>
			</div>
				<props:selectProperty name="${constants.codeDxProjectKey}" className="mediumField" enableFilter="true"></props:selectProperty>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Files to Upload:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.filesKey}" className="longField"></props:textProperty>
				<span class="smallNote">Files relative to the working directory to upload to Code Dx. Separate multiple files with a comma (,)</span>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Fail Build on Severity:</label>
		</th>
		<td>
			<span>
				<props:selectProperty name="${constants.codeDxSeverityKey}" className="mediumField">
					<props:option value="None"><c:out value="None"/></props:option>
					<props:option value="Critical"><c:out value="Critical"/></props:option>
					<props:option value="High"><c:out value="High"/></props:option>
					<props:option value="Medium"><c:out value="Medium"/></props:option>
					<props:option value="Low"><c:out value="Low"/></props:option>
				</props:selectProperty>
			</span>
		</td>
	</tr>
	<script type="text/javascript">
		function populateProjects(projects) {
			var $projects = $j(BS.Util.escapeId('${constants.codeDxProjectKey}'));
			$projects.empty();
			projects.forEach(function(project) {
				$projects.append($j('<option>', {
					value: project.id,
					text: project.name
				}));
			});
		}

		function getCodeDxProjects(isPageLoading){
			// Controls
			var $url = $j(BS.Util.escapeId('${constants.codeDxUrlKey}'));
			var $apiToken = $j(BS.Util.escapeId('${constants.codeDxAPITokenKey}'));
			var $urlError = $j(BS.Util.escapeId('error_${constants.codeDxUrlKey}'));
			var $apiTokenError = $j(BS.Util.escapeId('error_${constants.codeDxAPITokenKey}'));
			var $serverError = $j(BS.Util.escapeId('${constants.serverValidationErrorKey}'));
			var isErrors = false;

			// Clear error messages
			$urlError.text('');
			$apiTokenError.text('');
			$serverError.text('');

			var urlValue = BS.Util.trimSpaces($url.val());
			var apiTokenValue = BS.Util.trimSpaces($apiToken.val());

			if (!urlValue) {
				if (!isPageLoading) $urlError.text('Please enter a URL');
				isErrors = true;
			}

			if (!apiTokenValue) {
				if (!isPageLoading) $apiTokenError.text('Please enter an API Token');
				isErrors = true;
			}

			if (isErrors) {
				return;
			}

			var credentials = { codeDxUrl: urlValue, codeDxApiToken: apiTokenValue };

			$j.ajax({
				url: '/codedx.html',
				data: JSON.stringify(credentials),
				contentType: 'application/json',
				type: 'POST',
				success: function(data) {
					var responseData = JSON.parse(data);
					populateProjects(responseData.projects);
					if (isPageLoading) {
						var selectedProjectId = $j('#selected-project-id').find('strong').text();
						if (selectedProjectId) {
							var $projectInput = $j('#-ufd-teamcity-ui-codedx.selectedProject');
							var $selectedOption = $j(BS.Util.escapeId('${constants.codeDxProjectKey}') + ' option[value="' + selectedProjectId + '"]');
							var selectedText = $selectedOption.text();

							$projectInput.val(selectedText);
							$selectedOption.prop('selected', true);
						}
					}
					BS.enableJQueryDropDownFilter('codedx.selectedProject', {});
				},
				error: function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.status === 400) {
						$serverError.text(jqXHR.responseText);
					} else if (jqXHR.status === 403) {
						$serverError.text('API token does not have permission to access Code Dx projects');
					}
				}
			});
		}

		$j(function() {
			getCodeDxProjects(true);
		})
	</script>
</l:settingsGroup>