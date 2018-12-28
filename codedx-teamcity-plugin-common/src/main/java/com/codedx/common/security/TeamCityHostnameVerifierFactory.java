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
import javax.net.ssl.HttpsURLConnection;
import java.util.HashSet;

public class TeamCityHostnameVerifierFactory {
	public static HostnameVerifier getVerifier(String hostname) {
		HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
		HashSet<String> exceptions = new HashSet<>();
		exceptions.add(hostname);
		exceptions.add("www.example.com");

		return new TeamCityHostnameVerifier(hostnameVerifier, exceptions);
	}
}
