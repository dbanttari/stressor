package net.darylb.stressor;

import org.apache.http.client.HttpClient;

public abstract class HttpTest extends Test {

	private HttpClient httpClient;
	
	protected HttpClient getHttpClient() {
		if(this.httpClient == null) {
			httpClient = new ReferringHttpClient();
		}
		return httpClient;
	}

	@Override
	protected void onTestComplete() {
		super.onTestComplete();
		httpClient.getConnectionManager().shutdown();
	}

	
	
}
