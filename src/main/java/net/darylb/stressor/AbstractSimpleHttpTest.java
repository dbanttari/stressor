package net.darylb.stressor;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;

public abstract class AbstractSimpleHttpTest extends HttpGetAction {
	
	protected AbstractSimpleHttpTest(HttpClient httpClient, URI uri) {
		super(httpClient, uri);
	}

	private int uriPos = 0;
	
	abstract public String[] getURIList();
	
	public URI getNextAction() throws URISyntaxException {
		String[] uriList = getURIList();
		if(uriPos < uriList.length) {
			return null;
		}
		String uriString = uriList[uriPos++];
		URIBuilder builder = new URIBuilder(uriString);
		URI ret = builder.build();
		return ret;
	}
	
}
