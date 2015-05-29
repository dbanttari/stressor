package net.darylb.stressor.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.darylb.stressor.LoadTestContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpPostAction extends AbstractHttpAction {
	
	private static Logger log = LoggerFactory.getLogger(HttpPostAction.class);

	private static final int MAX_REDIRECTS = 10;
	protected boolean followRedirects = true;

	@Override
	public ActionResult call(LoadTestContext cx) {
		ActionResult actionResult = new ActionResult(this.getClass().getName());
		try {
			actionResult = doHttpRequest(cx);
		}
		catch (Throwable t) {
			log.warn("Test failure", t);
			actionResult = new ActionResult(this.getClass().getName());
			actionResult.setFail(t.getMessage());
			actionResult.setException(t);
		}
		return actionResult;
	}

	protected ActionResult doHttpRequest(LoadTestContext cx) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getName());
		// getForm must be called before creating HttpPost, as it may override the uri
		HttpEntity form = getForm(cx);
		String uriString = getUri(cx);
		URI uri;
		try {
			uri = new URI(uriString);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid uri: " + uriString, e);
		}
		HttpPost httpPost = new HttpPost(uri);
		if(form != null) {
			httpPost.setEntity(form);
		}
		addRequestHeaders(httpPost);
		HttpClient httpClient = getHttpClient(cx);
		log.debug("Posting to {}", uriString);
		response = httpClient.execute(httpPost);
		log.debug("Post to {} complete", uriString);
		int hitCount = 1;
		int statusCode = response.getStatusLine().getStatusCode();
		while(followRedirects && hitCount++ < MAX_REDIRECTS && (statusCode == 301 || statusCode == 302)) {
			EntityUtils.consume(response.getEntity());
			super.parseCookies(response);
			// redirects are always GETs.  Switch method
			String location = response.getLastHeader("Location").getValue();
			log.debug("Following {} redirect to {}", statusCode, location);
			HttpGet httpGet = new HttpGet(uri.resolve(location));
			response = httpClient.execute(httpGet);
			statusCode = response.getStatusLine().getStatusCode();
		}
		super.parseCookies(response);
		ret.setRequestCount(hitCount);
		ret.setStatus(response.getStatusLine().getStatusCode());
		String content = null;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			content = EntityUtils.toString(entity);
	        ret.setContent(content);
		}
		int responseCode = response.getStatusLine().getStatusCode();
		if(responseCode != 200) {
			String reason = "Response code " + responseCode;
			log.warn("Response status {} produced content: {}", response.getStatusLine(), content);
			ret.setFail(reason);
		}
		return ret;
	}

	protected HttpResponse getResponse() {
		return response;
	}

	public abstract HttpEntity getForm(LoadTestContext cx);
	
	public abstract String getUri(LoadTestContext cx);
	
}
