package net.darylb.stressor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedLoadTest extends LoadTest {

	Logger log = LoggerFactory.getLogger(TimedLoadTest.class);
	
	private final long startTick;
	private final long endTick;

	private long duration;
	
	public TimedLoadTest(LoadTestDefinition def, int numThreads, String duration) {
		this(def, numThreads, Util.parseDuration(duration));
	}
	
	public TimedLoadTest(LoadTestDefinition def, int numThreads, long duration) {
		super(def, numThreads);
		this.duration = duration;
		this.startTick = System.currentTimeMillis();
		this.endTick = this.startTick + duration;
	}

	@Override
	public void runTests(LoadTestContext cx, StoryFactory testFactory, List<LoadTestThread> threads, LoadTestResults ret) {
		while(System.currentTimeMillis() < getEndTick()) {
			try {
				Thread.sleep(getEndTick() - System.currentTimeMillis());
			}
			catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		for(LoadTestThread thread : threads) {
			thread.shutdown();
		}
		for(LoadTestThread thread : threads) {
			thread.join(60000);
		}
	}

	public long getEndTick() {
		return endTick;
	}

	@Override
	public double getProgressPct() {
		long elapsed = System.currentTimeMillis() - startTick;
		return 100.0 * Math.min(1.0, (double)elapsed / (double)duration);
	}

	@Override
	protected StoryFactory getWrappedStoryFactory(LoadTestDefinition def) {
		return def.getStoryFactory(def.getLoadTestContext());
	}

}
