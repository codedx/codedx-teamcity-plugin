package com.avi.codedx.agent;

import com.avi.codedx.common.CodeDxConstants;
import com.avi.codedx.agent.CodeDxBuildStatistics.Group;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CodeDxReportWriter {

	private final static String REPORT_FILE_NAME = "codedx-teamcity-report.html";

	private final static String CSS = "table{ border-collapse:collapse; width: 50%; margin:25px 25% 25px 25%; table-layout:fixed; }"
	                                + "td, th { border: 1px solid #dddddd; text-align:left; padding: 8px; }";

	private final static String HTML_BASE = "<html><head><style>%s</style><title>Code Dx Report</title></head><body>%s</body></html>";
	private final static String STATS_TABLE_BASE = "<table><tr><th>%s</th><th>Findings</th><th>Delta</th></tr>%s</table>";
	private final static String STATS_TABLE_ROWS_BASE = "<tr><td>%s</td><td>%s</td><td>%s</td></tr>";

	private final String reportArchiveName;
	private final CodeDxBuildStatistics statsBeforeAnalysis;
	private final CodeDxBuildStatistics statsAfterAnalysis;

	public CodeDxReportWriter(String reportArchiveName, CodeDxBuildStatistics statsBeforeAnalysis, CodeDxBuildStatistics statsAfterAnalysis) {
		this.reportArchiveName = reportArchiveName;
		this.statsBeforeAnalysis = statsBeforeAnalysis;
		this.statsAfterAnalysis = statsAfterAnalysis;
	}

	public void writeReport(File workspace) throws IOException {
		Path workspacePath = Paths.get(workspace.getCanonicalPath());
		File reportZip = createReportZip(workspacePath);

		try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(reportZip))) {
			File reportHtml = createHtmlReport();
			ZipEntry reportEntry = new ZipEntry(REPORT_FILE_NAME);
			zout.putNextEntry(reportEntry);

			try (FileInputStream fis = new FileInputStream(reportHtml)) {
				Archiver.addFileToZip(zout, fis);
			}
			zout.close();
		}

	}

	private File createReportZip(Path workspacePath) throws IOException {
		Path pathToReportZip = Paths.get(workspacePath.toString(), reportArchiveName + ".zip");
		File reportZip = pathToReportZip.toFile();

		if (reportZip.exists()) {
			reportZip.delete();
		}

		reportZip.createNewFile();

		return reportZip;
	}

	private File createHtmlReport() throws IOException {
		File tempHtmlFile = Files.createTempFile("", ".html").toFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempHtmlFile));

		tempHtmlFile.deleteOnExit();

		String severityTableRows = makeSeverityTableRows();
		String severityTable = String.format(STATS_TABLE_BASE, "Severity", severityTableRows);

		String statusTableRows = makeStatusTableRows();
		String statusTable = String.format(STATS_TABLE_BASE, "Status", statusTableRows);

		String html = String.format(HTML_BASE, CSS, severityTable + statusTable);

		bw.write(html);
		bw.close();
		return tempHtmlFile;
	}

	private String makeSeverityTableRows() {
		StringBuilder sb = new StringBuilder();
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.CRITICAL));
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.HIGH));
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.MEDIUM));
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.LOW));
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.INFO));
		sb.append(makeRow(Group.SEVERITY, CodeDxConstants.TOTAL));

		return sb.toString();
	}

	private String makeStatusTableRows() {
		StringBuilder sb = new StringBuilder();
		sb.append(makeRow(Group.STATUS, CodeDxConstants.FIXED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.MITIGATED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.IGNORED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.FALSE_POSITIVE));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.UNRESOLVED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.ESCALATED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.ASSIGNED));
		sb.append(makeRow(Group.STATUS, CodeDxConstants.TOTAL));

		return sb.toString();
	}

	private String makeRow(Group group, String rowName) {
		Integer previousNumberOfFindingsForName = statsBeforeAnalysis.getNumberOfFindingsForGroupAndName(group, rowName);
		Integer currentNumberOfFindingsForName = statsAfterAnalysis.getNumberOfFindingsForGroupAndName(group, rowName);
		Integer findingsDelta = currentNumberOfFindingsForName - previousNumberOfFindingsForName;

		return String.format(STATS_TABLE_ROWS_BASE, rowName, currentNumberOfFindingsForName.toString(), findingsDelta.toString());
	}
}