package com.avi.codedx.server;

import com.avi.codedx.common.CodeDxConstants;
import org.jetbrains.annotations.NotNull;

public class CodeDxConstantsBean {
	@NotNull
	public String getFilesKey() {
		return CodeDxConstants.SETTINGS_FILES;
	}

	@NotNull
	public String getProjectKey() {
		return CodeDxConstants.SETTINGS_PROJECT;
	}

	@NotNull
	public String getCodeDxUrlKey() {
		return CodeDxConstants.SETTINGS_CODEDX_URL_KEY;
	}

	@NotNull
	public String getCodeDxAPITokenKey() { return  CodeDxConstants.SETTINGS_API_TOKEN_KEY; }

	@NotNull
	public String getCodeDxProjectKey() { return CodeDxConstants.SETTNGS_CODEDX_PROJECT_KEY; }

	@NotNull
	public String getServerValidationErrorKey() { return CodeDxConstants.SETTINGS_SERVER_VALIDATION_ERROR_KEY; }
}