package net.darylb.stressor;

import org.apache.http.client.HttpClient;

public abstract class HttpTest extends Test {

	protected HttpTest(TestContext cx) {
		super(cx);
	}

	@Override
	protected void onTestComplete(TestContext cx) {
		super.onTestComplete(cx);
		((HttpClient)cx.getTestObject("http.client")).getConnectionManager().shutdown();
	}

	
	
}
