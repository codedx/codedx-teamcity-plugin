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

public enum CertificateAcceptance {

	/**
	 * The invalid certificate should be rejected.
	 */
	REJECT,

	/**
	 * The invalid certificate should be accepted on a short-term basis, e.g.
	 * for the duration of the session, or until the current JVM stops.
	 * .
	 */
	ACCEPT_TEMPORARILY,

	/**
	 * The invalid certificate should be accepted on a long-term basis, e.g. by
	 * adding the certificate to a custom KeyStore and persisting it to disk.
	 */
	ACCEPT_PERMANENTLY;

}
