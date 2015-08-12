package net.darylb.stressor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterTestDefinition extends LoadTestDefinition {

	private static Logger log = LoggerFactory.getLogger(RateLimiterTestDefinition.class);
	
	@Override
	public StoryFactory getStoryFactory(LoadTestContext cx) {
		return new RateLimiterTestStoryFactory(cx);
	}
	
	@Override
	public RateLimiterImpl getRateLimiter() {
		return new RateLimiterImpl(5, Interval.SECOND);
	}
	
	@Test
	public void test() {
		long startTick = System.currentTimeMillis();
		LoadTestContext cx = this.getLoadTestContext();
		cx.setRateLimiter(getRateLimiter());
		// 21 iterations, since the first has no wait, then 20 x 200ms = 4000ms
		System.out.println(new FixedLoadTest(cx, this.getStoryFactory(cx), 5, 21).run().toString());
		long dur = System.currentTimeMillis() - startTick;
		log.info("Total duration: {}ms", dur);
		assertTrue(dur > 3900L);
		assertTrue(dur < 4100L);
	}

}
