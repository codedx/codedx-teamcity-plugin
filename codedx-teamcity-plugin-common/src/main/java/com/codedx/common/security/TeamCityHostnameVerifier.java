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

package com.codedx.common.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.Set;

public class TeamCityHostnameVerifier implements HostnameVerifier {

	private final HostnameVerifier hostnameVerifier;
	private final Set<String> exceptions;

	public TeamCityHostnameVerifier(HostnameVerifier hostnameVerifier, Set<String> exceptions) {
		this.hostnameVerifier = hostnameVerifier;
		this.exceptions = exceptions;
	}

	@Override
	public boolean verify(String host, SSLSession sslSession) {
		try {
			hostnameVerifier.verify(host, sslSession);
		} catch (Exception e) {
			if (!exceptions.contains(host)) throw e;
		}
		return true;
	}
}
