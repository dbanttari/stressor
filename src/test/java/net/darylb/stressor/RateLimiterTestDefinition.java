package net.darylb.stressor;

import static org.junit.Assert.*;

import org.junit.Test;

public class RateLimiterTestDefinition extends TestDefinition {

	@Override
	public StoryFactory getStoryFactory(TestContext cx) {
		return new RateLimiterTestStoryFactory(cx);
	}
	
	@Override
	public RateLimiterImpl getRateLimiter() {
		return new RateLimiterImpl(5, RateLimiterImpl.Interval.SECOND);
	}
	
	@Test
	public void test() {
		long startTick = System.currentTimeMillis();
		System.out.println(new FixedLoadTest(this, 5, 4).run().toString());
		long dur = System.currentTimeMillis() - startTick;
		log.info("Total duration: {}ms", dur);
		assertTrue(dur > 3900L);
		assertTrue(dur < 4100L);
	}

}
