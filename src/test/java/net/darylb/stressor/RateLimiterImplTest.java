package net.darylb.stressor;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterImplTest {

	private static Logger log = LoggerFactory.getLogger(RateLimiterImplTest.class);
	
	@Test
	public void testSingleThread() {
		RateLimiter test = new RateLimiterImpl(20, RateLimiterImpl.Interval.SECOND);
		long startTick = System.currentTimeMillis();
		for(int i=0; i < 21; ++i) {
			test.limitRate();
		}
		// test should have taken about 1sec
		long dur = System.currentTimeMillis() - startTick;
		log.info("duration: {}ms", dur);
		assertTrue(dur > 900L);
		assertTrue(dur < 1100L);
	}

	@Test
	public void testMultiThread() {
		RateLimiter test = new RateLimiterImpl(20, RateLimiterImpl.Interval.SECOND);
		long startTick = System.currentTimeMillis();
		LinkedList<TestThread> testThreads = new LinkedList<TestThread>();
		for(int i=0; i < 21; ++i) {
			testThreads.add(new TestThread(test));
		}
		for(TestThread testThread : testThreads) {
			testThread.join();
		}
		// test should have taken about 1sec
		long dur = System.currentTimeMillis() - startTick;
		log.info("duration: {}ms", dur);
		assertTrue(dur > 900L);
		assertTrue(dur < 1100L);
	}
	
	
	public class TestThread implements Runnable {

		private RateLimiter rateLimiter;
		private Thread thread;

		public TestThread(RateLimiter rateLimiter) {
			this.rateLimiter = rateLimiter;
			this.thread = new Thread(this);
			thread.setDaemon(true);
			thread.setName("TestThread");
			thread.start();
		}

		@Override
		public void run() {
			rateLimiter.limitRate();
		}
		
		public void join() {
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
