package net.darylb.stressor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustingX509TrustManager implements X509TrustManager {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TrustingX509TrustManager.class);
	
	private X509Certificate cert;

	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] xcs, String type) throws CertificateException {
		this.cert = xcs[0];
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public X509Certificate getCert() {
		return cert;
	}

}