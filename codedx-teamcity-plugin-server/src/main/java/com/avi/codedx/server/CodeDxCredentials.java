package com.avi.codedx.server;

public class CodeDxCredentials {

	private String codeDxUrl;
	private String codeDxApiToken;
	private String fingerprint;

	public void setCodeDxUrl(String codeDxUrl) {
		if(codeDxUrl.endsWith("/")) {
			codeDxUrl = codeDxUrl.substring(0, codeDxUrl.length() - 1);
		}
		this.codeDxUrl = codeDxUrl;
	}

	public String getCodeDxUrl() {
		return this.codeDxUrl;
	}

	public void setCodeDxApiToken(String codeDxApiToken) {
		this.codeDxApiToken = codeDxApiToken;
	}

	public String getCodeDxApiToken() {
		return this.codeDxApiToken;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getFingerprint() {
		return this.fingerprint;
	}
}
