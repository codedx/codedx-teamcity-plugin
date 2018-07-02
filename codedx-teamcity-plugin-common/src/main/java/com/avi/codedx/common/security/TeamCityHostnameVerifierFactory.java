package com.avi.codedx.common.security;

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
