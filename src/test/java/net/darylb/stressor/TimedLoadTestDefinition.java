package net.darylb.stressor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimedLoadTestDefinition extends TestDefinition {

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
		System.out.println(new TimedLoadTest(this, 5, "10s").run().toString());
		long dur = System.currentTimeMillis() - startTick;
		log.info("Total duration: {}ms", dur);
		assertTrue(dur > 10000L);
		assertTrue(dur < 12000L);
	}
	
}
