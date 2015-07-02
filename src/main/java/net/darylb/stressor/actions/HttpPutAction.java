package net.darylb.stressor.actions;

import java.net.URI;
import java.net.URISyntaxException;

import net.darylb.stressor.LoadTestContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpPutAction extends HttpAction {

	private static Logger log = LoggerFactory.getLogger(HttpPutAction.class);
	
	@Override
	public ActionResult call(LoadTestContext cx) {
		String uriString = getUri(cx);
		try {
			URI uri = new URI(uriString);
			log.debug("Storing {}", uri);
			ActionResult actionResult = doHttpRequest(cx, uri);
			log.debug("Completed {}", uri);
			return actionResult;
		}
		catch (URISyntaxException e) {
			log.warn("Invalid URI: {}", uriString);
			throw new RuntimeException(e);
		}
	}

	protected ActionResult doHttpRequest(LoadTestContext cx, URI uri) {
		ActionResult ret = new ActionResult(this.getClass().getName());
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setEntity(getHttpEntity());
		addRequestHeaders(httpPut);
		try {
			response = getHttpClient(cx).execute(httpPut);
			super.parseCookies(response);
			int status = response.getStatusLine().getStatusCode();
			ret.setStatus(status);
			if(status != 200 && status != 201) {
				String reason = "Response code " + status;
				log.warn("Upload to {} failed: {}", uri, reason);
				ret.setFail(reason);
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String content = EntityUtils.toString(entity);
		        ret.setContent(content);
			}
		}
		catch (Throwable t) {
			log.warn("Upload to {} failed:", uri, t);
			ret.setFail(t.getMessage());
			ret.setException(t);
		}
		return ret;
	}

	public abstract HttpEntity getHttpEntity();

	public abstract String getUri(LoadTestContext cx);

	protected HttpResponse getResponse() {
		return response;
	}
	
}
