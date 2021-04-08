package com.tmb.oneapp.productsexpservice.config;

import org.springframework.stereotype.Component;

@Component
public class HttpsUrlHostnameIgnoreConfig {

	private HttpsUrlHostnameIgnoreConfig() {
		
	}
	static {
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
	}
}