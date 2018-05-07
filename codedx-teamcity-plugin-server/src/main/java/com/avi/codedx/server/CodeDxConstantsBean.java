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
	public String getCodeDxAPITokenKey() {
		return  CodeDxConstants.SETTINGS_API_TOKEN_KEY;
	}

	@NotNull
	public String getCodeDxProjectKey() {
		return CodeDxConstants.SETTINGS_CODEDX_PROJECT_KEY;
	}

	@NotNull
	public String getServerValidationErrorKey() {
		return CodeDxConstants.SETTINGS_SERVER_VALIDATION_ERROR_KEY;
	}

	@NotNull
	public String getCodeDxSeverityKey() {
		return CodeDxConstants.SETTINGS_CODEDX_SEVERITY_KEY;
	}

	@NotNull
	public String getOnlyNewFindingsKey() {
		return CodeDxConstants.SETTINGS_ONLY_NEW_FINDINGS_KEY;
	}

	@NotNull
	public String getAnalysisNameKey() {
		return CodeDxConstants.SETTINGS_ANALYSIS_NAME_KEY;
	}

	@NotNull
	public String getToolOutputFilesKey() {
		return CodeDxConstants.SETTINGS_TOOL_OUTPUT_FILES_KEY;
	}

	@NotNull
	public String getFilesExcludedKey() {
		return CodeDxConstants.SETTINGS_FILES_EXCLUDED_KEY;
	}
}
