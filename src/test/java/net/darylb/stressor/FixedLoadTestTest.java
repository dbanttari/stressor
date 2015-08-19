package net.darylb.stressor;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(EasyMockRunner.class) 
public class FixedLoadTestTest extends EasyMockSupport {

	private static Logger log = LoggerFactory.getLogger(FixedLoadTestTest.class);

	@Mock
	private LoadTestDefinition def;
	
	@Test
	public void test() throws Exception {
		final int ITERATIONS = 20;
		final int THREADS = 2;

		// configure mock
		LoadTestContext cx = new MockLoadTestContext();
		expect(def.getLoadTestContext())
			.andReturn(cx)
			.atLeastOnce();
		expect(def.getRateLimiter())
			.andReturn(new RateLimiterImpl(1100/ITERATIONS));
		expect(def.getStoryFactory(cx))
			.andReturn(new MockStoryFactory(cx))
			.atLeastOnce();
		expect(def.getPendingRequestHandlerLocator(cx))
			.andReturn(null);
		replayAll();
		
		final FixedLoadTest test = new FixedLoadTest(def, THREADS, ITERATIONS);
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
		verifyAll();
	}
}
