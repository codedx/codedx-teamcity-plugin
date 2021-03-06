<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="com.codedx.server.CodeDxConstantsBean"/>

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
			<label>API key:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.codeDxAPITokenKey}" className="longField"></props:textProperty>
				<span class="error" id="error_${constants.codeDxAPITokenKey}"></span>
			</span>
			<span>
				<span class="error" id="${constants.serverValidationErrorKey}"></span>
			</span>
		</td>
	</tr>
	<tr>
	<tr class="advancedSetting">
		<th>
			<label>SHA1 fingerprint:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.sha1FingerprintKey}" className="longField"></props:textProperty>
				<span class="smallNote">Please remove spaces and symbols like (:) from the fingerprint, it should only contain numbers and letters</span>
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
				<props:selectProperty name="${constants.codeDxProjectKey}" className="longField" enableFilter="true"></props:selectProperty>
				<button id="reload" type="button">Reload</button>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Source and binaries:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.filesKey}" className="longField"></props:textProperty>
				<span class="smallNote">Files relative to the working directory to zip and upload to Code Dx. Separate multiple files with a comma (,)</span>
			</span>
			<span>
				<span class="error" id="${constants.sourceAndBinariesWarningKey}">Warning: No source and binaries specified</span>
			</span>
		</td>
	</tr>
	<tr class="advancedSetting">
		<th>
			<label>Files to exclude:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.filesExcludedKey}" className="longField"></props:textProperty>
				<span class="smallNote">Files relative to the working directory to exclude from the zip uploaded to Code Dx. Separate multiple files with a comma (,)</span>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Tool output files:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.toolOutputFilesKey}" className="longField"></props:textProperty>
				<span class="smallNote">File paths can be absolute or relative to the working directory. Separate multiple files with a comma (,)</span>
			</span>
			<span>
				<span class="error" id="${constants.toolOutputsWarningKey}">Warning: No tool output files specified</span>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Analysis name:</label>
		</th>
		<td>
			<props:textProperty name="${constants.analysisNameKey}" className="longField"></props:textProperty>
		</td>
	</tr>
	<tr>
		<c:set var="onclick">
			$('${constants.reportArchiveNameKey}').disabled = !this.checked;
			$('${constants.codeDxSeverityKey}').disabled = !this.checked;
			$('${constants.onlyNewFindingsKey}').disabled = !this.checked;
			BS.VisibilityHandlers.updateVisibility('${constants.reportArchiveNameKey}');
			BS.VisibilityHandlers.updateVisibility('${constants.codeDxSeverityKey}');
			BS.VisibilityHandlers.updateVisibility('${constants.onlyNewFindingsKey}');
		</c:set>
		<th>
			<label for="${constants.waitForResultsKey}">Wait for results:</label>
			<span class="smallNote"></span>
		</th>
		<td>
			<props:checkboxProperty name="${constants.waitForResultsKey}" onclick="${onclick}"/>
		</td>
	</tr>
	<tr>
		<th>
			<label>Report archive name:</label>
		</th>
		<td>
			<span>
				<props:textProperty name="${constants.reportArchiveNameKey}" className="longField"></props:textProperty>
				<span class="smallNote">Please provide a unique and static name for the report archive file. If the name is not unique, the report may be overwritten by other Code Dx build steps. If the name is not static, the report tab will not display.</span>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Fail build on severity:</label>
		</th>
		<td>
			<span>
				<props:selectProperty name="${constants.codeDxSeverityKey}" className="mediumField">
					<props:option value="0"><c:out value="None"/></props:option>
					<props:option value="1"><c:out value="Info or higher"/></props:option>
					<props:option value="2"><c:out value="Low or higher"/></props:option>
					<props:option value="3"><c:out value="Medium or higher"/></props:option>
					<props:option value="4"><c:out value="High or higher"/></props:option>
					<props:option value="5"><c:out value="Critical"/></props:option>
				</props:selectProperty>
			</span>
			<span>
		</td>
	</tr>
	<tr>
		<th>
			<label for="${constants.onlyNewFindingsKey}">Only fail on new findings:</label>
		</th>
		<td>
			<props:checkboxProperty name="${constants.onlyNewFindingsKey}"/>
		</td>
	</tr>
	<script type="text/javascript">
		function populateProjects(projectList) {
			var $projects = $j(BS.Util.escapeId('${constants.codeDxProjectKey}'));
			$projects.empty();

			projectList.sort((a,b) => {
				return a.name.localeCompare(b.name)
			})

			projectList.forEach(function(project) {
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
			var $fingerprint = $j(BS.Util.escapeId('${constants.sha1FingerprintKey}'));
			var $projects = $j(BS.Util.escapeId('${constants.codeDxProjectKey}'));
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
			var fingerprintValue = BS.Util.trimSpaces($fingerprint.val());

			if (!urlValue) {
				if (!isPageLoading) $urlError.text('Please enter a URL');
				isErrors = true;
			}

			if (!apiTokenValue) {
				if (!isPageLoading) $apiTokenError.text('Please enter an API Token');
				isErrors = true;
			}

			if (isErrors) {
				$projects.empty();
				return;
			}

			var credentials = { codeDxUrl: urlValue,
			                    codeDxApiToken: apiTokenValue,
			                    fingerprint: fingerprintValue
			                  };

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
					switch(jqXHR.status) {
						case 400:
							$urlError.text(jqXHR.responseText);
							break;
						case 403:
							$apiTokenError.text(jqXHR.responseText);
							break;
						case 404:
							$urlError.text(jqXHR.responseText);
							break;
						default:
							$urlError.text(jqXHR.responseText);
					}
					$projects.empty();
				}
			});
		}

		function displayFileWarning($textBox, $warningLabel) {
			var textValue = BS.Util.trimSpaces($textBox.val());
			if(textValue) {
				$warningLabel.hide();
			} else {
				$warningLabel.show();
			}
		}

		$j(function() {
			var isWaitForResults = $('codedx.waitForResults').checked;
			var $url = $j(BS.Util.escapeId('${constants.codeDxUrlKey}'));
			var $apiToken = $j(BS.Util.escapeId('${constants.codeDxAPITokenKey}'));
			var $fingerprint = $j(BS.Util.escapeId('${constants.sha1FingerprintKey}'));
			var $reload = $('reload');
			var $sourceAndBinaries = $j(BS.Util.escapeId('${constants.filesKey}'));
			var $toolOutputs = $j(BS.Util.escapeId('${constants.toolOutputFilesKey}'));
			var $sourceAndBinariesWarning = $j(BS.Util.escapeId('${constants.sourceAndBinariesWarningKey}'));
			var $toolOutputsWarning = $j(BS.Util.escapeId('${constants.toolOutputsWarningKey}'));

			$('${constants.reportArchiveNameKey}').disabled = !isWaitForResults;
			$('${constants.codeDxSeverityKey}').disabled = !isWaitForResults;
			$('${constants.onlyNewFindingsKey}').disabled = !isWaitForResults;
			BS.VisibilityHandlers.updateVisibility('${constants.reportArchiveNameKey}');
			BS.VisibilityHandlers.updateVisibility('${constants.codeDxSeverityKey}');
			BS.VisibilityHandlers.updateVisibility('${constants.onlyNewFindingsKey}');

			getCodeDxProjects(true);

			$url.on('input', function() { getCodeDxProjects(false) });
			$apiToken.on('input', function() { getCodeDxProjects(false) });
			$fingerprint.on('input', function() { getCodeDxProjects(false) });
			$reload.on('click', function() { getCodeDxProjects(false) });
			$sourceAndBinaries.on('input', function() { displayFileWarning($sourceAndBinaries, $sourceAndBinariesWarning) });
			$toolOutputs.on('input', function() { displayFileWarning($toolOutputs, $toolOutputsWarning) });

			setTimeout(function() {
				$sourceAndBinaries.trigger('input');
				$toolOutputs.trigger('input');
			}, 1000);
		});
	</script>
</l:settingsGroup>