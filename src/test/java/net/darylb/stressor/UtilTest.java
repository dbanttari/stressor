package net.darylb.stressor;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testParseDuration() {
		assertEquals(10000L, Util.parseDuration("10s"));
		assertEquals(600000L, Util.parseDuration("10m"));
	}

}
