package net.darylb.stressor.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.darylb.stressor.LoadTestContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpGetAction extends AbstractHttpAction {

	private static final Logger log = LoggerFactory.getLogger(HttpGetAction.class);
	
	private static final int MAX_REDIRECTS = 10;
	private String referer;
	
	@Override
	public ActionResult call(LoadTestContext cx) {
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
	
	protected ActionResult doHttpRequest(LoadTestContext cx) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getName());
		String uriString = getUri(cx);
		URI uri;
		try {
			uri = new URI(uriString);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid uri: " + uriString, e);
		}
		HttpGet httpGet = new HttpGet(uri);
		if(referer!=null) {
			httpGet.setHeader("Referer", referer);
		}
		HttpClient httpClient = this.getHttpClient(cx);
		HttpClientContext context = null;
		Credentials credentials = this.getCredentials(cx);

		if(credentials==null) {
			log.debug("Getting {}", uri);
			response = httpClient.execute(httpGet);
		}
		else {
			context = getContext(uri, credentials);
			log.debug("Getting {} with http auth", uri);
			response = httpClient.execute(httpGet, context);
		}
		int requestCount = 1;
		while( (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302) && requestCount++ < MAX_REDIRECTS ) {
			EntityUtils.consume(response.getEntity());
			super.parseCookies(response);
			String newLocation = response.getFirstHeader("Location").getValue();
			URI newUri;
			try {
				newUri = new URI(newLocation);
			}
			catch (URISyntaxException e) {
				log.error("Unable to parse redirect URI {}", uri);
				throw new RuntimeException(e);
			}
			// use relative URL resolution if scheme (eg 'http') is not defined
			if(newUri.getScheme() == null) {
				// resolve new location relative to old
				log.debug("Redirect relative to {}", newLocation);
				uri = uri.resolve(newLocation);
			}
			else {
				uri = newUri;
				log.debug("Redirect absolute to {}", uri);
				if(credentials != null) {
					context = getContext(uri, credentials);
				}
			}
			httpGet = new HttpGet(uri);
			if(credentials==null) {
				response = httpClient.execute(httpGet);
			}
			else {
				response = httpClient.execute(httpGet, context);
			}
		}
		log.debug("Get from {} complete", uri);
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

	public abstract String getUri(LoadTestContext cx);
	
	protected HttpResponse getResponse() {
		return response;
	}

}
