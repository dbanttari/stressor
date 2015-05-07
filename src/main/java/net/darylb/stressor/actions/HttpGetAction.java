package net.darylb.stressor.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.darylb.stressor.TestContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpGetAction extends AbstractHttpAction {

	private static final Logger log = LoggerFactory.getLogger(HttpGetAction.class);
	
	private static final int MAX_REDIRECTS = 10;
	private String referer;
	
	@Override
	public ActionResult call(TestContext cx) {
		ActionResult actionResult = new ActionResult(this.getName());
		try {
			actionResult = doHttpRequest(cx);
		}
		catch (Throwable t) {
			log.error("Request to {} Failed", this.toString(), t);
			actionResult = new ActionResult(this.getName());
			actionResult.setFail(t.getMessage());
			actionResult.setException(t);
		}
		return actionResult;
	}
	
	protected ActionResult doHttpRequest(TestContext cx) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getName());
		String uriString = getUri(cx);
		URI uri;
		try {
			uri = new URI(uriString);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid uri: " + uriString, e);
		}
		HttpGet httpget = new HttpGet(uri);
		if(referer!=null) {
			httpget.setHeader("Referer", referer);
		}
		HttpClient httpClient = super.getHttpClient(cx);
		log.debug("Getting {}", uriString);
		response = httpClient.execute(httpget);
		log.debug("Get from {} complete", uriString);
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

	public abstract String getUri(TestContext cx);
	
	protected HttpResponse getResponse() {
		return response;
	}

}
