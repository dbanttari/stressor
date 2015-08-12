package net.darylb.stressor.switchboard;

import static org.junit.Assert.*;

import org.junit.Test;

public class PendingRequestHandlerLocatorTest {

	@Test
	public void testGetToken() {
		assertEquals("1234", PendingRequestHandlerLocator.getToken(null, "/foo/bar/1234", null));
		assertEquals("1234", PendingRequestHandlerLocator.getToken(null, "/foo?bar=1234", null));
	}

}
