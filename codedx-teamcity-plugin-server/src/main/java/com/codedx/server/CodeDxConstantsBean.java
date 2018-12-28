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

	@NotNull
	public String getSha1FingerprintKey() {
		return CodeDxConstants.SETTINGS_SHA1_FINGERPRINT_KEY;
	}

	@NotNull
	public String getWaitForResultsKey() {
		return CodeDxConstants.SETTINGS_WAIT_FOR_RESULTS_KEY;
	}

	@NotNull
	public String getReportArchiveNameKey() {
		return CodeDxConstants.SETTINGS_REPORT_ARCHIVE_NAME_KEY;
	}

	@NotNull
	public String getSourceAndBinariesWarningKey() {
		return CodeDxConstants.SETTINGS_SOURCE_AND_BINARIES_WARNING_KEY;
	}

	@NotNull
	public String getToolOutputsWarningKey() {
		return CodeDxConstants.SETTINGS_TOOL_OUTPUTS_WARNING_KEY;
	}
}
