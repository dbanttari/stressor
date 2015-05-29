package net.darylb.stressor;

import static org.junit.Assert.*;
import net.darylb.stressor.actions.MockAction;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedLoadTestTest {
	
	private static Logger log = LoggerFactory.getLogger(TimedLoadTestTest.class);

	@Test
	public void test() throws Exception {
		MockTestContext cx = new MockTestContext();
		StoryFactory fac = new MockStoryFactory(cx);
		final TimedLoadTest test = new TimedLoadTest(cx, fac, 1, 1000);
		RateLimiterImpl rateLimiter = new RateLimiterImpl(100L); // 100 ms
		cx.setRateLimiter(rateLimiter);
		MockAction.resetCount();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				log.debug("test starting");
				test.run();
				log.debug("test ending");
			}
			
		});
		t.start();
		log.debug("Current progress {}%", test.getProgressPct());
		assertTrue("starting at less than 1%", test.getProgressPct() < 1.0);
		Thread.sleep(450);
		log.debug("Current progress {}%", test.getProgressPct());
		assertTrue("middle at least 40%", test.getProgressPct() >= 40.0);
		assertTrue("middle at most 60%", test.getProgressPct() <= 60.0);
		Thread.sleep(550);
		log.debug("Current progress {}%", test.getProgressPct());
		assertTrue("end at 100%", test.getProgressPct() == 100.0);
		log.debug("Counted {} iterations", MockAction.getCount());
		assertTrue("ran 9+ iterations", MockAction.getCount() >= 9);
		assertTrue("ran 11 or fewer iterations", MockAction.getCount() <= 11);
	}
	
}
