package net.darylb.stressor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedLoadTest extends LoadTest {

	Logger log = LoggerFactory.getLogger(TimedLoadTest.class);
	
	private final long endTick;
	
	public TimedLoadTest(TestDefinition testRunner, int numThreads, String duration) {
		this(testRunner, numThreads, parseDuration(duration));
	}
	
	private static long parseDuration(String duration) {
		long num = Long.parseLong(duration.substring(0, duration.length()-1));
		char interval = duration.charAt(duration.length()-1);
		switch(interval) {
		case 's':
			return num * 1000L;
		case 'm':
			return num * 60L * 1000L;
		case 'h':
			return num * 60L * 60L * 1000L;
		case 'd':
			return num * 24L * 60L * 60L * 1000L;
		}
		throw new IllegalArgumentException("Invalid duration string.  should be ");
	}

	public TimedLoadTest(TestDefinition testRunner, int numThreads, long duration) {
		super(testRunner, numThreads, -1);
		this.endTick = System.currentTimeMillis() + duration;
	}

	@Override
	public void runTests(TestContext cx, StoryFactory testFactory, List<TestThread> threads, TestResults ret) {
		while(System.currentTimeMillis() < getEndTick()) {
			try {
				Thread.sleep(getEndTick() - System.currentTimeMillis());
			}
			catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		for(TestThread thread : threads) {
			thread.shutdown();
		}
		for(TestThread thread : threads) {
			thread.join(60000);
		}
	}

	public long getEndTick() {
		return endTick;
	}
	
}
