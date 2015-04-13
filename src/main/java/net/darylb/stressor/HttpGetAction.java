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
	private HttpClient httpClient;
	private String referer;

	public HttpGetAction(HttpClient httpClient, URI uri) {
		this.httpClient = httpClient;
		this.uri = uri;
	}
	
	public HttpGetAction(HttpClient httpClient, String string) {
		this.httpClient = httpClient;
		this.uri = URI.create(string);
	}
	
	public HttpGetAction(HttpClient httpClient, String string, String referer) {
		this.httpClient = httpClient;
		this.referer = referer;
		this.uri = URI.create(string);
	}
	
	@Override
	public ActionResult call() {
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
		long totalRequestDuration = 0;
		long reqStart = System.currentTimeMillis();
		HttpResponse response = httpClient.execute(httpget);
		totalRequestDuration += System.currentTimeMillis() - reqStart;
		int requestCount = 1;
		while( (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302) && requestCount++ < MAX_REDIRECTS ) {
			EntityUtils.consume(response.getEntity());
			uri = uri.resolve(response.getFirstHeader("Location").getValue());
			HttpGet httpGet = new HttpGet(uri);
			referer = uri.toString();
			httpGet.addHeader("referer", referer);
			reqStart = System.currentTimeMillis();
			response = httpClient.execute(httpGet);
			totalRequestDuration += System.currentTimeMillis() - reqStart;
		}
		ret.setRequestCount(requestCount);
		ret.setRequestDuration(totalRequestDuration);
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
		    ret.setValid(validate(content));
		}
		return ret;
	}

	public URI getUri() {
		return uri;
	}
}
