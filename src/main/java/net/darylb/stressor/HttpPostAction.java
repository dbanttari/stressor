package net.darylb.stressor;

import java.io.IOException;
import java.net.URI;

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
	private final URI uri;
	private HttpEntity httpEntity;
	private HttpClient httpClient;

	public HttpPostAction(HttpClient httpClient, URI uri, HttpEntity httpEntity) {
		this.httpClient = httpClient;
		this.uri = uri;
		this.httpEntity = httpEntity;
	}
	
	public HttpPostAction(HttpClient httpClient, String uri, String urlEncodedFormParams) {
		this(httpClient,
				URI.create(uri),
				new StringEntity(urlEncodedFormParams, ContentType.create("application/x-www-form-urlencoded"))
		);
	}

	@Override
	public ActionResult call() {
		ActionResult actionResult = new ActionResult(this.getClass().getName());
		try {
			log("Fetching " + getUri().toString());
			actionResult = doHttpRequest(getUri());
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

	protected ActionResult doHttpRequest(URI uri) throws ClientProtocolException, IOException {
		ActionResult ret = new ActionResult(this.getClass().getName());
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(httpEntity);
		HttpResponse response = httpClient.execute(httpPost);
		int hitCount = 1;
		while(hitCount++ < MAX_REDIRECTS && (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302)) {
			EntityUtils.consume(response.getEntity());
			// redirects are always GETs.  Switch method
			HttpGet httpGet = new HttpGet(uri.resolve(response.getFirstHeader("Location").getValue()));
			response = httpClient.execute(httpGet);
		}
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
	        ret.setValid(validate(content));
		}
		return ret;
	}

	
	protected void log(String s) {
		//System.out.println("HttpPostAction " + Thread.currentThread().getName() + ": " + s);
	}

	public URI getUri() {
		return uri;
	}

}
