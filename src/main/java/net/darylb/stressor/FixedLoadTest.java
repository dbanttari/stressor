package net.darylb.stressor;

import java.util.List;

public class FixedLoadTest extends LoadTest {
	
	public FixedLoadTest(TestDefinition testRunner, int numThreads, int numIterationsPerThread) {
		super(testRunner, numThreads, numIterationsPerThread);
	}

	@Override
	public void runTests(TestContext cx, StoryFactory testFactory, List<TestThread> threads, TestResults ret) {
		for(TestThread thread : threads) {
			thread.join();
		}
	}

}
