package net.darylb.stressor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedLoadTestDefinition extends LoadTestDefinition {

	private static Logger log = LoggerFactory.getLogger(TimedLoadTestDefinition.class);

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
		System.out.println(new TimedLoadTest(cx, this.getStoryFactory(cx), 5, "10s").run().toString());
		long dur = System.currentTimeMillis() - startTick;
		log.info("Total duration: {}ms", dur);
		assertTrue(dur > 10000L);
		assertTrue(dur < 12000L);
	}
	
}
