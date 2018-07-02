package com.avi.codedx.common;

public final class CodeDxConstants {
	public static final String RUNNER_TYPE = "Code Dx";

	public static final String SETTINGS_FILES = "codedx.files";
	public static final String SETTINGS_FILES_EXCLUDED_KEY = "codedx.excludedFiles";
	public static final String SETTINGS_PROJECT = "codedx.project";
	public static final String SETTINGS_CODEDX_URL_KEY = "codedx.url";
	public static final String SETTINGS_API_TOKEN_KEY = "codedx.apiToken";
	public static final String SETTINGS_CODEDX_PROJECT_KEY = "codedx.selectedProject";
	public static final String SETTINGS_SERVER_VALIDATION_ERROR_KEY = "codedx.serverValidationError";
	public static final String SETTINGS_CODEDX_SEVERITY_KEY = "codedx.severity";
	public static final String SETTINGS_ONLY_NEW_FINDINGS_KEY = "codedx.onlyNewFindings";
	public static final String SETTINGS_ANALYSIS_NAME_KEY = "codedx.analysisName";
	public static final String SETTINGS_TOOL_OUTPUT_FILES_KEY = "codedx.toolOutputFiles";
	public static final String SETTINGS_SHA1_FINGERPRINT_KEY = "codedx.sha1Fingerprint";
	public static final String SETTINGS_WAIT_FOR_RESULTS_KEY = "codedx.waitForResults";
	public static final String SETTINGS_REPORT_ARCHIVE_NAME_KEY = "codedx.reportArchiveName";

	public static final String CRITICAL = "Critical";
	public static final String HIGH = "High";
	public static final String MEDIUM = "Medium";
	public static final String LOW = "Low";
	public static final String INFO = "Info";

	public static final String FIXED = "Fixed";
	public static final String MITIGATED = "Mitigated";
	public static final String IGNORED = "Ignored";
	public static final String FALSE_POSITIVE = "False Positive";
	public static final String UNRESOLVED = "Unresolved";
	public static final String ESCALATED = "Escalated";
	public static final String ASSIGNED = "Assigned";
	public static final String NEW = "New";
	public static final String TOTAL = "Total";

	public static final String RUNNER_DISPLAY_NAME = "Code Dx";
	public static final String RUNNER_DESCRIPTION = "Code Dx analysis runner";

	public static final String API_ERROR_MESSAGE = "An error occurred while trying to communicate with Code Dx's API";
	public static final String IO_ERROR_MESSAGE = "An error occurred while working with files";
	public static final String INTERRUPT_ERROR_MESSAGE = "An error caused the Code Dx build step to be interrupted";
	public static final String VERIFICATION_ERROR_MESSAGE = "There are verification errors and an analysis could not be triggered.";
}
