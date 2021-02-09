package com.tmb.oneapp.productsexpservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tmb.common.logger.TMBLogger;
import feign.Client;

/**
 * SSL certificate Integration
 *
 */
@Configuration
public class SSLConfiguration {

	private static TMBLogger<SSLConfiguration> logger = new TMBLogger<>(SSLConfiguration.class);

	private final String keyStorePath;
	private final String trustStorePath;
	private final String keyStorePw;
	private final String trustStorePw;
	private final String keyPass;
	private final String trustAllCertificate;
	private final String keyManagerAlgorithm;

	private TrustManager[] trustAllCerts = null;
	private SSLContext sslContext = null;

	SSLConfiguration(@Value("${server.ssl.key-store}") final String keyStorePath,
					 @Value("${server.ssl.trust-store}") final String trustStorePath,
					 @Value("${server.ssl.key-store-password}") final String keyStorePw,
					 @Value("${server.ssl.trust-store-password}") final String trustStorePw,
					 @Value("${server.ssl.keypass}") final String keyPass,
					 @Value("${server.ssl.trustAllCertificate}") final String trustAllCertificate,
					 @Value("${server.ssl.keyManagerAlgorithm}") final String keyManagerAlgorithm) {
		this.keyStorePath = keyStorePath;
		this.trustStorePath = trustStorePath;
		this.keyStorePw = keyStorePw;
		this.trustStorePw = trustStorePw;
		this.keyPass = keyPass;
		this.trustAllCertificate = trustAllCertificate;
		this.keyManagerAlgorithm = keyManagerAlgorithm;

	}

	/**
	 * @return sslcontext for https request
	 */
	@Bean
	public Client getfeignClient() {
		setupSslContext();
		return new Client.Default(sslContext.getSocketFactory(), null);
	}

	/**
	 * sslContext method to load keystore and intialize
	 */
	public void setupSslContext() {

		boolean trustall = false;
		try {
			System.getProperty(keyStorePath);
			trustAllCerts = new TrustManager[] { new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			if (trustAllCertificate.equalsIgnoreCase("True")) {
				trustall = true;
			}
			sslContext = initializeSSLContext(keyStorePath, keyStorePw, trustStorePath, trustStorePw, keyPass,
					trustall);

		} catch (Exception exp) {
			logger.error("ConfigException exception occurred while reading the config file : {}", exp);
		}
	}

	/**
	 * initializeSSLContext method to initialize ssl context
	 *
	 * @param keyStorePath
	 * @param pwKeyStore
	 * @param trustStorePath
	 * @param pwTrustStore
	 * @param keyPass
	 * @param trustall
	 * @return
	 */
	private SSLContext initializeSSLContext(final String keyStorePath, final String pwKeyStore,
											final String trustStorePath, final String pwTrustStore, final String keyPass, final boolean trustall) {
		logger.info(" In ", ProductsExpServiceConstant.INITIALIZE_SSL_CONTEXT);
		char[] keyStorePwd = pwKeyStore.toCharArray();
		char[] trustStorePwd = pwTrustStore.toCharArray();
		char[] keyPw = keyPass.toCharArray();
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextInt();
		KeyStore ks = null;
		KeyManagerFactory kmf = null;
		KeyStore ts = null;
		try (final InputStream fis = this.getClass().getResourceAsStream(keyStorePath)) {
			ks = KeyStore.getInstance("JKS");
			if (ks != null)
				ks.load(fis, keyStorePwd);

			logger.info(ProductsExpServiceConstant.INITIALIZE_SSL_CONTEXT, " KMF keystorepw loaded.");

			kmf = KeyManagerFactory.getInstance(keyManagerAlgorithm);

			if (kmf != null)
				kmf.init(ks, keyPw);

			logger.info(ProductsExpServiceConstant.INITIALIZE_SSL_CONTEXT, " KMF init done.");
			ts = KeyStore.getInstance("JKS");
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| IOException exp) {
			logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, exp.getMessage());
		}
		sslContext = null;
		try (final InputStream tfis = this.getClass().getResourceAsStream(trustStorePath)) {
			if (ts != null) {
				ts.load(tfis, trustStorePwd);

				TrustManagerFactory tmf = TrustManagerFactory.getInstance(keyManagerAlgorithm);
				tmf.init(ts);

				logger.info(ProductsExpServiceConstant.INITIALIZE_SSL_CONTEXT, " Truststore initialized");
				sslContext = SSLContext.getInstance("TLS");
				if (kmf != null) {
					if (trustall)
						sslContext.init(kmf.getKeyManagers(), trustAllCerts, secureRandom);
					else
						sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
				}
			}
		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException
				| KeyManagementException exp) {
			logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, exp);
		}
		if ((sslContext == null)) {
			logger.info(ProductsExpServiceConstant.INITIALIZE_SSL_CONTEXT, " sslContext is null");
			System.exit(-1);
		}
		return sslContext;
	}

}
