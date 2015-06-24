package net.darylb.stressor.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import net.darylb.stressor.ReferringHttpClient;
import net.darylb.stressor.LoadTestContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpAction extends Action {

	protected HttpResponse response;
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AbstractHttpAction.class);
	
	LinkedHashMap<String, HttpCookie> cookies;
	
	protected void parseCookies(HttpResponse response) {
		if(cookies==null) {
			cookies = new LinkedHashMap<String, HttpCookie>();
		}
		for(Header header : response.getAllHeaders()) {
			if(header.getName().equalsIgnoreCase("Set-Cookie")) {
				HttpCookie c = new HttpCookie(header.getValue());
				//log.debug("cookie " + c.name.toLowerCase() + ": " + c.value);
				cookies.put(c.name.toLowerCase(), c);
			}
		}
	}
	
	public LinkedHashMap<String, HttpCookie> getCookies() {
		return cookies;
	}

	public void addCookie(String name, String value) {
		try {
			addHeader("Set-Cookie", name + "=" + URLEncoder.encode(value, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			// will never happen
			e.printStackTrace();
		}
	}
	
	public String getNewCookieValue(String cookieName) {
		return getNewCookieValue(cookieName, null);
	}
	
	public String getNewCookieValue(String cookieName, String dfault) {
		HttpCookie c = cookies.get(cookieName.toLowerCase());
		return c == null ? dfault : c.value;
	}
	
	private LinkedHashMap<String,String> headers = new LinkedHashMap<String,String>();

	protected void addHeader(String name, String value) {
		headers.put(name, value);
	}

	protected void addRequestHeaders(HttpRequestBase req) {
		for(Map.Entry<String, String> header : headers.entrySet() ) {
			req.addHeader(header.getKey(), header.getValue());
		}
	}
	
	byte[] readContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		return readContentFromEntity(entity, 16384);
	}
	byte[] readContentFromEntity(HttpEntity entity, int maxLen) throws IllegalStateException, IOException {
	    byte[] buf = new byte[maxLen];
	    int pos = 0;
	    InputStream instream = entity.getContent();
	    try {
			int bytes;
	        while( (bytes=instream.read(buf, pos, buf.length-pos)) > 1) {
	        	pos += bytes;
	        }
	    } finally {
	        instream.close();
	    }
	    return Arrays.copyOfRange(buf, 0, pos);
	}
	
	HttpClient getHttpClient(LoadTestContext cx) {
		ReferringHttpClient ret;
		if(cx.hasStoryObject(Props.HTTP_CLIENT)) {
			ret = (ReferringHttpClient)cx.getStoryObject(Props.HTTP_CLIENT);
		}
		else {
			ret = new ReferringHttpClient();
			cx.setStoryObject(Props.HTTP_CLIENT, ret);
		}
		return ret;
	}

	private static volatile int responseCount=0;
	public void logResponse(LoadTestContext cx, String content) {
		cx.logFile("response-"+Integer.toString(++responseCount)+".html", content);
	}

	public CredentialsProvider getCredentialsProvider(LoadTestContext cx) {
		return null;
	}

}
