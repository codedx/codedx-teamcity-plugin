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
