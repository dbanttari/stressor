package net.darylb.stressor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedLoadTest extends LoadTest {
	
	private static Logger log = LoggerFactory.getLogger(FixedLoadTest.class);
	
	private int numTests;
	private LoadTestDefinition def;
	private FixedLoadTestStoryFactory wrappedStoryFactory;

	public FixedLoadTest(LoadTestDefinition def, int numThreads, int numTests) {
		super(def, numThreads);
		this.def = def;
		this.numTests = numTests;
		log.debug("Configured for {} tests", numTests);
	}

	@Override
	public void runTests(LoadTestContext cx, StoryFactory testFactory, List<LoadTestThread> threads, LoadTestResults ret) {
		for(LoadTestThread thread : threads) {
			thread.join();
		}
	}

	@Override
	public double getProgressPct() {
		FixedLoadTestStoryFactory fac = (FixedLoadTestStoryFactory)getWrappedStoryFactory(def);
		return fac.getProgressPct();
	}

	@Override
	protected StoryFactory getWrappedStoryFactory(LoadTestDefinition def) {
		if(wrappedStoryFactory == null) {
			wrappedStoryFactory = new FixedLoadTestStoryFactory(def.getStoryFactory(def.getLoadTestContext()), numTests);
		}
		return wrappedStoryFactory;
	}

}
