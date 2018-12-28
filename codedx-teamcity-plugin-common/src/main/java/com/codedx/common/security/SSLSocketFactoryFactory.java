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

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class SSLSocketFactoryFactory {
	public static SSLSocketFactory getFactory(String fingerprint) throws GeneralSecurityException {
		SingleCertManager manager = new SingleCertManager("floopydoop");

		FingerprintStrategy certificateStrategy = new FingerprintStrategy(fingerprint);

		List<X509TrustManager> trustManagersForComposite = new LinkedList<>();
		X509TrustManager systemTrustManager = getDefaultTrustManager();
		ReloadableX509TrustManager customTrustManager = new ReloadableX509TrustManager(manager, certificateStrategy);
		trustManagersForComposite.add(systemTrustManager);
		trustManagersForComposite.add(customTrustManager);
		X509TrustManager trustManager = new CompositeX509TrustManager(trustManagersForComposite);

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(null, new TrustManager[] { trustManager }, null);

		return sslContext.getSocketFactory();
	}

	private static X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory defaultFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		defaultFactory.init((KeyStore) null);

		TrustManager[] managers = defaultFactory.getTrustManagers();
		for (TrustManager mgr : managers) {
			if (mgr instanceof X509TrustManager) {
				return (X509TrustManager) mgr;
			}
		}
		return null;
	}

}
