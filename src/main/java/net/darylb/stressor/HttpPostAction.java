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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpPostAction extends AbstractHttpAction {
	
	private static Logger log = LoggerFactory.getLogger(HttpPostAction.class);

	private static final int MAX_REDIRECTS = 10;
	protected boolean followRedirects = true;
	HttpResponse response;

	@Override
	public ActionResult call(TestContext cx) {
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

	protected ActionResult doHttpRequest(TestContext cx) throws ClientProtocolException, IOException {
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
			ret.setFail(reason);
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String content = EntityUtils.toString(entity);
	        ret.setContent(content);
		}
		return ret;
	}

	protected HttpResponse getResponse() {
		return response;
	}

	public abstract HttpEntity getForm(TestContext cx);
	
	public abstract String getUri(TestContext cx);
	
}
