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
				<button type="button" onclick="getCodeDxProjects()">Verify</button>
			</span>
		</td>
	</tr>
	<tr>
		<th>
			<label>Project:</label>
		</th>
		<td>
			<span>
				<props:selectProperty name="${constants.codeDxProjectKey}" className="mediumField"></props:selectProperty>
			</span>
		</td>
	</tr>
	<script type="text/javascript">
		function getCodeDxProjects(){
			var $url = $j(BS.Util.escapeId('${constants.codeDxUrlKey}'));
			var $apiToken = $j(BS.Util.escapeId('${constants.codeDxAPITokenKey}'));

			var urlValue = BS.Util.trimSpaces($url.val());
			var apiTokenValue = BS.Util.trimSpaces($apiToken.val());

			if (!urlValue) {

			}

			var credentials = { codeDxUrl: urlValue, codeDxApiToken: apiTokenValue };

			$j.ajax({
				url: '/codedx.html',
				data: JSON.stringify(credentials),
				contentType: 'application/json',
				type: 'POST',
				success: function(data) {
					console.log(data);
				}
			});
		}
	</script>
</l:settingsGroup>