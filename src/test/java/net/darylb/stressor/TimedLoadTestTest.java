package net.darylb.stressor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*; 
import net.darylb.stressor.actions.MockAction;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(EasyMockRunner.class) 
public class TimedLoadTestTest extends EasyMockSupport {
	
	private static Logger log = LoggerFactory.getLogger(TimedLoadTestTest.class);

	@Mock
	private LoadTestDefinition def;
	
	@Test
	public void test() throws Exception {
		// configure mock
		LoadTestContext cx = new MockLoadTestContext();
		expect(def.getLoadTestContext())
			.andReturn(cx)
			.atLeastOnce();
		expect(def.getRateLimiter())
			.andReturn(new RateLimiterImpl(100L));
		expect(def.getStoryFactory(cx))
			.andReturn(new MockStoryFactory(cx));
		expect(def.getPendingRequestHandlerLocator())
			.andReturn(null);
		replayAll();
		
		// run test
		final TimedLoadTest test = new TimedLoadTest(def, 1, 1000);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				log.debug("test starting");
				test.run();
				log.debug("test ending");
			}
			
		});
		MockAction.resetCount();
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
		// after the first not-delayed action, we should have time for 10 more
		assertEquals(11, MockAction.getCount());
		verifyAll();
	}
	
}
