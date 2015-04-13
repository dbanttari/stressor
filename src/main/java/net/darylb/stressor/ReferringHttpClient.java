package net.darylb.stressor;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class ReferringHttpClient implements HttpClient {

	private final HttpClient httpClient;
	private String referer = null;
	
	public ReferringHttpClient() {
		ClientConnectionManager connManager = new BasicClientConnectionManager();
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpClient defaultHttpClient = new DefaultHttpClient(connManager, params);
		TrustingX509TrustManager tm = new TrustingX509TrustManager();
		httpClient = WebClientDevWrapper.wrapClient(defaultHttpClient, tm);
	}

	@Override
	public HttpParams getParams() {
		return httpClient.getParams();
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return httpClient.getConnectionManager();
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
		applyReferer(request);
		return httpClient.execute(request);
	}

	private void applyReferer(HttpRequest request) {
		if(referer!=null) {
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
