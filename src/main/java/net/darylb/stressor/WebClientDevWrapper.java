package net.darylb.stressor;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;

/*
 This code is public domain: you are free to use, link and/or modify it in any way you want, for all purposes including commercial applications. 
 http://javaskeleton.blogspot.com/2010/07/avoiding-peer-not-authenticated-with.html
 */
public class WebClientDevWrapper {


	static X509HostnameVerifier verifier = new X509HostnameVerifier() {

		@Override
		public void verify(String string, SSLSocket ssls) throws IOException {
		}

		@Override
		public void verify(String string, java.security.cert.X509Certificate xc)
				throws SSLException {
			System.out.println(string);
			System.out.println(xc.getSubjectDN().getName());
		}

		@Override
		public void verify(String string, String[] strings, String[] strings1)
				throws SSLException {
		}

		@Override
		public boolean verify(String string, SSLSession ssls) {
			return true;
		}

	};
	
	@SuppressWarnings("deprecation")
	public static HttpClient wrapClient(HttpClient base, TrustingX509TrustManager tm) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(verifier);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
