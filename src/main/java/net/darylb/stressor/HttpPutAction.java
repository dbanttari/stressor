package net.darylb.stressor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;

public class HttpPutAction extends AbstractHttpAction {

	private final URI uri;
	private HttpEntity httpEntity;
	HttpResponse response;

	public HttpPutAction(String path) {
		try {
			this.uri = new URI(path);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpPutAction(String path, HttpEntity httpEntity) {
		this(path);
		this.httpEntity = httpEntity;
	}
	
	public HttpPutAction(String uri, File file) {
		this(uri, new FileEntity(file));
		addHeader("Content-Type", "application/octet-stream");
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
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setEntity(httpEntity);
		addRequestHeaders(httpPut);
		response = getHttpClient(cx).execute(httpPut);
		super.parseCookies(response);
		int status = response.getStatusLine().getStatusCode();
		ret.setStatus(status);
		if(status != 200 && status != 201) {
			String reason = "Response code " + status;
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
		//System.out.println("HttpPutAction " + Thread.currentThread().getName() + ": " + s);
	}

	public URI getUri() {
		return uri;
	}

	protected HttpResponse getResponse() {
		return response;
	}
	
}
