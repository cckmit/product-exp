package com.tmb.oneapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@ComponentScan(basePackages = { "com.tmb.oneapp", "com.tmb.common" })
public class ProductsExpServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductsExpServiceApplication.class);
		setCertificate();
	}
	static void setCertificate() {
		String keyStoreFile = "oneapp-dev.tmbbank.local.jks";
		if (null == System.getProperty("javax.net.ssl.keyStore")) {
			System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
			System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
		}
		if (null == System.getProperty("javax.net.ssl.trustStore")) {
			System.setProperty("javax.net.ssl.trustStore", keyStoreFile);
			System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		}
	}
}
