package com.avi.codedx.common;

public interface CodeDxConstants {
	String RUNNER_TYPE = "Code Dx";

	String SETTINGS_FILES = "codedx.files";
	String SETTINGS_FILES_EXCLUDED_KEY = "codedx.excludedFiles";
	String SETTINGS_PROJECT = "codedx.project";
	String SETTINGS_CODEDX_URL_KEY = "codedx.url";
	String SETTINGS_API_TOKEN_KEY = "codedx.apiToken";
	String SETTINGS_CODEDX_PROJECT_KEY = "codedx.selectedProject";
	String SETTINGS_SERVER_VALIDATION_ERROR_KEY = "codedx.serverValidationError";
	String SETTINGS_CODEDX_SEVERITY_KEY = "codedx.severity";
	String SETTINGS_ONLY_NEW_FINDINGS_KEY = "codedx.onlyNewFindings";
	String SETTINGS_ANALYSIS_NAME_KEY = "codedx.analysisName";
	String SETTINGS_TOOL_OUTPUT_FILES_KEY = "codedx.toolOutputFiles";
	String SETTINGS_SHA1_FINGERPRINT_KEY = "codedx.sha1Fingerprint";
	String SETTINGS_WAIT_FOR_RESULTS_KEY = "codedx.waitForResults";
	String SETTINGS_REPORT_ARCHIVE_NAME_KEY = "codedx.reportArchiveName";

	String CRITICAL = "Critical";
	String HIGH = "High";
	String MEDIUM = "Medium";
	String LOW = "Low";
	String INFO = "Info";

	String FIXED = "Fixed";
	String MITIGATED = "Mitigated";
	String IGNORED = "Ignored";
	String FALSE_POSITIVE = "False Positive";
	String UNRESOLVED = "Unresolved";
	String ESCALATED = "Escalated";
	String ASSIGNED = "Assigned";
	String NEW = "New";
	String TOTAL = "Total";

	String RUNNER_DISPLAY_NAME = "Code Dx";
	String RUNNER_DESCRIPTION = "Code Dx analysis runner";
}
