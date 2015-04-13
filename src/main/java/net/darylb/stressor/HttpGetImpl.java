package net.darylb.stressor;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGetImpl extends HttpGetAction {

	private Logger log = LoggerFactory.getLogger(HttpGetImpl.class);
	private String expected;
	private String url;
	
	public HttpGetImpl(HttpClient httpClient, String url, String expected) {
		super(httpClient, url);
		this.url = url;
		this.expected = expected;
	}

	protected String validate(String buf) {
		if(buf.indexOf(expected) == -1) {
			log.debug("{} : '{}' not found in {}", url, expected, new String(buf));
			return "'" + expected + "' not found in response";
		}
		else {
			return null;
		}
	}
}
