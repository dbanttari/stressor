package net.darylb.stressor.actions;

import static org.junit.Assert.*;
import net.darylb.stressor.actions.Cookie;

import org.junit.Test;

public class CookieTest {

	@Test
	public void test() {
		Cookie c;
		c = new Cookie("FORM_TOKEN=2f2923aeb09ffbb9f0f2153803837ef9;domain=.invisionapp.com;expires=Fri, 15-May-2015 19:53:15 GMT;path=/");
		assertEquals("FORM_TOKEN", c.name);
		assertEquals("2f2923aeb09ffbb9f0f2153803837ef9", c.value);
		assertEquals(".invisionapp.com", c.domain);
		c = new Cookie("XSRF-TOKEN=5e61f35e5bc3c8873fda87b9b8b001ea;domain=.invisionapp.com;expires=Fri, 07-Apr-2045 19:53:15 GMT;path=/");
		assertEquals("XSRF-TOKEN", c.name);
		assertEquals("5e61f35e5bc3c8873fda87b9b8b001ea", c.value);
		assertEquals(".invisionapp.com", c.domain);
	}

}
