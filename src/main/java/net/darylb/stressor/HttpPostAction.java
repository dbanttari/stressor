package net.darylb.stressor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class HttpPostAction extends AbstractHttpAction {

	private static final int MAX_REDIRECTS = 10;
	private URI uri;
	private HttpEntity httpEntity;
	protected boolean followRedirects = true;
	HttpResponse response;

	public HttpPostAction(String uri) {
		try {
			this.uri = new URI(uri);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpPostAction(TestContext cx, String uri, HttpEntity httpEntity) {
		this(uri);
		this.httpEntity = httpEntity;
	}
	
	public HttpPostAction(TestContext cx, String uri, String urlEncodedFormParams) {
		this(cx, uri, new StringEntity(urlEncodedFormParams, ContentType.create("application/x-www-form-urlencoded")));
	}

	protected void setHttpEntity(HttpEntity entity) {
		this.httpEntity = entity;
	}
	
	@Override
	public ActionResult call(TestContext cx) {
		ActionResult actionResult = new ActionResult(this.getClass().getName());
		try {
			log("Fetching " + getUri().toString());
			actionResult = doHttpRequest(cx, getUri());
			//log("Completed " + uri.toString());
		}
		catch (Throwable t) {
			log("Request Failed:");
			t.printStackTrace();
			actionResult = new ActionResult(this.getClass().getName());
			actionResult.setFail(t.getMessage());
			actionResult.setException(t);
		}
		return actionResult;
	}

	protected ActionResult doHttpRequest(TestContext cx, URI uri) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getClass().getName());
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(httpEntity);
		addRequestHeaders(httpPost);
		HttpClient httpClient = getHttpClient(cx);
		response = httpClient.execute(httpPost);
		int hitCount = 1;
		while(followRedirects && hitCount++ < MAX_REDIRECTS && (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302)) {
			EntityUtils.consume(response.getEntity());
			super.parseCookies(response);
			// redirects are always GETs.  Switch method
			HttpGet httpGet = new HttpGet(uri.resolve(response.getLastHeader("Location").getValue()));
			response = httpClient.execute(httpGet);
		}
		super.parseCookies(response);
		ret.setRequestCount(hitCount);
		ret.setStatus(response.getStatusLine().getStatusCode());
		if(response.getStatusLine().getStatusCode() != 200) {
			String reason = "Response code " + response.getStatusLine().getStatusCode();
			log("Test failure: " + reason);
			ret.setFail(reason);
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String content = EntityUtils.toString(entity);
	        ret.setContent(content);
		}
		return ret;
	}

	protected void log(String s) {
		//System.out.println("HttpPostAction " + Thread.currentThread().getName() + ": " + s);
	}

	public URI getUri() {
		return uri;
	}
	
	public void setUri(String uri) throws URISyntaxException {
		this.uri = new URI(uri);
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	protected HttpResponse getResponse() {
		return response;
	}
	
}
