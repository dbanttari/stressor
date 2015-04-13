package net.darylb.stressor;

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author daryl.banttari
 *
 * content of test file: (in case wwwroot/sandbox gets deleted again)
 * 
 * Referrer is <cfoutput>#cgi.http_referer#</cfoutput>
 * <br><br>
 * <a href="referertest.cfm">How about now?</a>
 *
 */
public class ReferringHttpClientTest {

	private static final Logger log = LoggerFactory.getLogger(ReferringHttpClientTest.class);
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		String url = "http://localhost/referertest.cfm";
		
		HttpClient test = new ReferringHttpClient();
		URI uri = URI.create(url);
		HttpUriRequest request = new HttpGet(uri);
		
		HttpResponse ret = test.execute(request);
		String content = EntityUtils.toString(ret.getEntity());
		log.debug(content);
		assertFalse(content.contains(url));

		ret = test.execute(request);
		content = EntityUtils.toString(ret.getEntity());
		log.debug(content);
		assertTrue(content.contains(url));
		
	}

}
