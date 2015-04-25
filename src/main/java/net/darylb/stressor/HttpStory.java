package net.darylb.stressor;

import org.apache.http.client.HttpClient;

public abstract class HttpStory extends Story {

	@Override
	protected void onTestComplete(TestContext cx) {
		super.onTestComplete(cx);
		if(cx.hasTestObject("http.client")) {
			((HttpClient)cx.getTestObject("http.client")).getConnectionManager().shutdown();
		}
	}

}
