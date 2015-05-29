package net.darylb.stressor;

import java.util.List;

public class FixedLoadTest extends LoadTest {
	
	public FixedLoadTest(LoadTestContext cx, StoryFactory storyFactory, int numThreads, int numTests) {
		super(cx, new FixedLoadTestStoryFactory(storyFactory, numTests), numThreads);
	}

	@Override
	public void runTests(LoadTestContext cx, StoryFactory testFactory, List<LoadTestThread> threads, LoadTestResults ret) {
		for(LoadTestThread thread : threads) {
			thread.join();
		}
	}

	@Override
	public double getProgressPct() {
		FixedLoadTestStoryFactory fac = (FixedLoadTestStoryFactory)super.getStoryFactory();
		return fac.getProgressPct();
	}

}
