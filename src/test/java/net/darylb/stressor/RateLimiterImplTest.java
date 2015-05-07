package net.darylb.stressor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterImplTest {

	private static Logger log = LoggerFactory.getLogger(RateLimiterImplTest.class);
	
	@Test
	public void test() {
		RateLimiter test = new RateLimiterImpl(50);
		long startTick = System.currentTimeMillis();
		for(int i=0; i < 20; ++i) {
			test.limitRate();
		}
		// test should have taken about 1sec
		long dur = System.currentTimeMillis() - startTick;
		log.info("duration: {}ms", dur);
		assertTrue(dur > 900L);
		assertTrue(dur < 1100L);
	}

}
