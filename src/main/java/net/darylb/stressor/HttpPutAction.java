package net.darylb.stressor;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPutAction extends AbstractHttpAction {

	private static Logger log = LoggerFactory.getLogger(HttpPutAction.class);
	
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
		log.debug("Storing " + getUri().toString());
		ActionResult actionResult = doHttpRequest(cx, getUri());
		log.debug("Completed " + uri.toString());
		return actionResult;
	}

	protected ActionResult doHttpRequest(TestContext cx, URI uri) {
		ActionResult ret = new ActionResult(this.getClass().getName());
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setEntity(httpEntity);
		addRequestHeaders(httpPut);
		try {
			response = getHttpClient(cx).execute(httpPut);
			super.parseCookies(response);
			int status = response.getStatusLine().getStatusCode();
			ret.setStatus(status);
			if(status != 200 && status != 201) {
				String reason = "Response code " + status;
				log.warn("Test failure: " + reason);
				ret.setFail(reason);
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String content = EntityUtils.toString(entity);
		        ret.setContent(content);
			}
		}
		catch (Throwable t) {
			log.warn("Request Failed:", t);
			ret.setFail(t.getMessage());
			ret.setException(t);
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
