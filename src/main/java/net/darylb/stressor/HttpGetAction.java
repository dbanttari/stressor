package net.darylb.stressor;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGetAction extends AbstractHttpAction {

	private static final Logger log = LoggerFactory.getLogger(HttpGetAction.class);
	
	private static final int MAX_REDIRECTS = 10;
	private final URI uri;
	private String referer;
	private HttpResponse response;

	private TestContext cx;

	public HttpGetAction(TestContext cx, String path) {
		this.cx = cx;
		this.uri = URI.create(path);
	}
	
	public HttpGetAction(TestContext cx, String path, String referer) {
		this(cx, path);
		this.referer = referer;
	}
	
	@Override
	public ActionResult call(TestContext cx) {
		ActionResult actionResult = new ActionResult(this.getClass().getName());
		try {
			log.trace("Fetching " + uri.toString());
			actionResult = doHttpRequest(uri);
			//log("Completed " + uri.toString());
		}
		catch (Throwable t) {
			log.error("Request to {} Failed", this.toString(), t);
			actionResult = new ActionResult(this.getClass().getName());
			actionResult.setFail(t.getMessage());
			actionResult.setException(t);
		}
		return actionResult;
	}
	
	protected ActionResult doHttpRequest(URI uri) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getClass().getName());
		HttpGet httpget = new HttpGet(uri);
		if(referer!=null) {
			httpget.setHeader("Referer", referer);
		}
		HttpClient httpClient = super.getHttpClient(cx);
		response = httpClient.execute(httpget);
		int requestCount = 1;
		while( (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302) && requestCount++ < MAX_REDIRECTS ) {
			EntityUtils.consume(response.getEntity());
			super.parseCookies(response);
			uri = uri.resolve(response.getFirstHeader("Location").getValue());
			HttpGet httpGet = new HttpGet(uri);
			response = httpClient.execute(httpGet);
		}
		super.parseCookies(response);
		ret.setRequestCount(requestCount);
		ret.setStatus(response.getStatusLine().getStatusCode());
		if(response.getStatusLine().getStatusCode() != 200) {
			String reason = "Response code " + response.getStatusLine().getStatusCode();
			log.warn("Test failure: {}", reason);
			ret.setFail(reason);
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String content = EntityUtils.toString(entity);
		    ret.setContent(content);
		}
		return ret;
	}

	public URI getUri() {
		return uri;
	}
	
	protected HttpResponse getResponse() {
		return response;
	}

}
