package net.darylb.stressor;

import static org.junit.Assert.*;
import net.darylb.stressor.actions.MockAction;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedLoadTestTest {

	private static Logger log = LoggerFactory.getLogger(FixedLoadTestTest.class);

	@Test
	public void test() throws Exception {
		final int ITERATIONS = 20;
		final int THREADS = 2;
		MockTestContext cx = new MockTestContext();
		MockAction.resetCount();
		StoryFactory fac = new MockStoryFactory(cx);
		final FixedLoadTest test = new FixedLoadTest(cx, fac, THREADS, ITERATIONS);
		RateLimiterImpl rateLimiter = new RateLimiterImpl(1100/ITERATIONS); // however many iterations, get it done in ~1sec
		cx.setRateLimiter(rateLimiter);
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
		assertTrue("starting at less than 10%", test.getProgressPct() < 10.0);
		Thread.sleep(400);
		log.debug("Current progress {}%", test.getProgressPct());
		assertTrue("middle at least 40%", test.getProgressPct() >= 40.0);
		assertTrue("middle at most  60%", test.getProgressPct() <= 60.0);
		Thread.sleep(600);
		log.debug("Current progress {}%", test.getProgressPct());
		assertTrue("end at 100%", test.getProgressPct() == 100.0);
		t.join();
		assertEquals(ITERATIONS, MockAction.getCount());
	}
}
