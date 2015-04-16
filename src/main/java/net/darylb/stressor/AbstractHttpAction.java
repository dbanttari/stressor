package net.darylb.stressor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public abstract class AbstractHttpAction extends Action {

	LinkedHashMap<String, Cookie> cookies;
	
	protected void parseCookies(HttpResponse response) {
		if(cookies==null) {
			cookies = new LinkedHashMap<String, Cookie>();
			for(Header header : response.getAllHeaders()) {
				if(header.getName().equalsIgnoreCase("Set-Cookie")) {
					Cookie c = new Cookie(header.getValue());
					System.out.println("Cookie " + c.name + ":" + c.value);
					cookies.put(c.name.toLowerCase(), c);
				}
			}
		}
	}
	
	public LinkedHashMap<String, Cookie> getCookies() {
		return cookies;
	}
	
	public String getNewCookieValue(String cookieName) {
		return getNewCookieValue(cookieName, null);
	}
	
	public String getNewCookieValue(String cookieName, String dfault) {
		Cookie c = cookies.get(cookieName.toLowerCase());
		System.out.println("c=" + (c==null ? "null" : c.toString()));
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
	
	public AbstractHttpAction(TestContext cx) {
		super(cx);
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

}
