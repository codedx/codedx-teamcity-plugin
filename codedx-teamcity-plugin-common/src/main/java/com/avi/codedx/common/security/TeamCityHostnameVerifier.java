package com.avi.codedx.common.security;

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
