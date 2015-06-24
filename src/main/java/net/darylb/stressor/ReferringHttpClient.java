package net.darylb.stressor;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class ReferringHttpClient implements HttpClient {

	private static final Logger log = LoggerFactory.getLogger(ReferringHttpClient.class);

	public static SSLSocketFactory s_sslSocketFactory = null;

	private final HttpClient httpClient;
	private String referer = null;

	public ReferringHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setConnectionTimeToLive(1, TimeUnit.MINUTES);
		builder.setRedirectStrategy(new RedirectStrategy() {
			@Override
			public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
				return false;
			}

			@Override
			public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
				return null;
			}
		});
		SSLContext context;
		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new TrustingX509TrustManager() }, null);
			builder.setSSLContext(context);
			httpClient = builder.build();
		}
		catch (NoSuchAlgorithmException | KeyManagementException e) {
			log.error("This should never happen", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * deprecated, but required by interface.
	 */
	@Override
	public HttpParams getParams() {
		return httpClient.getParams();
	}

	/**
	 * deprecated, but required by interface.
	 */
	@Override
	public ClientConnectionManager getConnectionManager() {
		return httpClient.getConnectionManager();
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(request);
	}

	/**
	 * adds previous request's uri as referrer (if applicable); remembers current request's uri for next call
	 * @param request the current request
	 */
	private void applyReferer(HttpRequest request) {
		if (referer != null) {
			request.setHeader("Referer", referer);
		}
		setReferer(request.getRequestLine().getUri());
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(request, context);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(target, request);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(target, request, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(request, responseHandler);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(request, responseHandler);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(target, request, responseHandler);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException,
			ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(target, request, responseHandler, context);
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

}
