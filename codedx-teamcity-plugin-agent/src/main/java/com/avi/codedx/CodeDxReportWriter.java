package com.avi.codedx;

import com.avi.codedx.common.CodeDxConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CodeDxReportWriter {

	private final static String REPORT_FILE_NAME = "codedx-teamcity-report.html";

	private final static String BODY_REPLACEMENT = "$$body$$";
	private final static String TABLE_TITLE_REPLACEMENT = "$$tableTile$$";
	private final static String TABLE_ROWS_REPLACEMENT = "$$tableRows$$";
	private final static String ROW_NAME_REPLACEMENT = "$$severityName$$";
	private final static String NUMBER_OF_FINDINGS_REPLACEMENT = "$$numberOfFindings$$";
	private final static String FINDINGS_DELTA_REPLACEMENT = "$$findingsDelta$$";

	private final static String CSS = "";

	private final static String HTML_BASE = "<html><head><style>" + CSS + "</style><title>Code Dx Report</title></head><body>"+ BODY_REPLACEMENT +"</body></html>";
	private final static String STATS_TABLE_BASE = "<table><tr><th>" + TABLE_TITLE_REPLACEMENT + "</th><th>Findings</th><th>Delta</th></tr>" + TABLE_ROWS_REPLACEMENT +"</table>";
	private final static String STATS_TABLE_ROWS_BASE = "<tr><td>" + ROW_NAME_REPLACEMENT + "</td><td>" + NUMBER_OF_FINDINGS_REPLACEMENT + "</td><td>"+ FINDINGS_DELTA_REPLACEMENT + "</td></tr>";

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
		String severityTable = STATS_TABLE_BASE.replace(TABLE_TITLE_REPLACEMENT, "Severity").replace(TABLE_ROWS_REPLACEMENT, severityTableRows);

		String statusTableRows = makeStatusTableRows();
		String statusTable = STATS_TABLE_BASE.replace(TABLE_TITLE_REPLACEMENT, "Status").replace(TABLE_ROWS_REPLACEMENT, statusTableRows);


		String html = HTML_BASE.replace(BODY_REPLACEMENT, severityTable + statusTable);

		bw.write(html);
		bw.close();
		return tempHtmlFile;
	}

	private String makeSeverityTableRows() {
		StringBuilder sb = new StringBuilder();
		sb.append(makeRow(CodeDxConstants.CRITICAL));
		sb.append(makeRow(CodeDxConstants.HIGH));
		sb.append(makeRow(CodeDxConstants.MEDIUM));
		sb.append(makeRow(CodeDxConstants.LOW));
		sb.append(makeRow(CodeDxConstants.INFO));

		return sb.toString();
	}

	private String makeStatusTableRows() {
		StringBuilder sb = new StringBuilder();
		sb.append(makeRow(CodeDxConstants.FIXED));
		sb.append(makeRow(CodeDxConstants.MITIGATED));
		sb.append(makeRow(CodeDxConstants.IGNORED));
		sb.append(makeRow(CodeDxConstants.FALSE_POSITIVE));
		sb.append(makeRow(CodeDxConstants.UNRESOLVED));
		sb.append(makeRow(CodeDxConstants.ESCALATED));
		sb.append(makeRow(CodeDxConstants.ASSIGNED));

		return sb.toString();
	}

	private String makeRow(String rowName) {
		Integer previousNumberOfFindingsForSeverity = statsBeforeAnalysis.getNumberOfFindingsForName(rowName);
		Integer currentNumberOfFindingsForSeverity = statsAfterAnalysis.getNumberOfFindingsForName(rowName);
		Integer findingsDelta = currentNumberOfFindingsForSeverity - previousNumberOfFindingsForSeverity;

		return STATS_TABLE_ROWS_BASE
			.replace(ROW_NAME_REPLACEMENT, rowName)
			.replace(NUMBER_OF_FINDINGS_REPLACEMENT, currentNumberOfFindingsForSeverity.toString())
			.replace(FINDINGS_DELTA_REPLACEMENT, findingsDelta.toString());
	}
}
