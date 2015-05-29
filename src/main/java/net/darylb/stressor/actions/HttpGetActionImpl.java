package net.darylb.stressor.actions;

import net.darylb.stressor.LoadTestContext;

public class HttpGetActionImpl extends HttpGetAction {

	private String expected;
	private String url;
	
	public HttpGetActionImpl(String url) {
		this(url, null);
	}

	public HttpGetActionImpl(String url, String expected) {
		this.url = url;
		this.expected = expected;
	}

	@Override
	public void validate(LoadTestContext cx, String content) throws ActionValidationException {
		if(expected != null) {
			if(content.indexOf(expected) == -1) {
				invalid("'" + expected + "' not found in response");
			}
		}
	}

	@Override
	public String getUri(LoadTestContext cx) {
		return url;
	}
}
