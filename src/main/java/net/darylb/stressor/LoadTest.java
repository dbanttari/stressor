package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoadTest {

	private static Logger log = LoggerFactory.getLogger(LoadTest.class);
	
	private final int numThreads;
	private final TestContext cx;
	private final StoryFactory storyFactory;
	private final int numIterationsPerThread;

	public LoadTest(TestDefinition testRunner, int numThreads, int numIterationsPerThread) {
		this.numThreads = numThreads;
		this.numIterationsPerThread = numIterationsPerThread;
		cx = testRunner.getTestContext();
		cx.setNumThreads(numThreads);
		cx.setRateLimiter(testRunner.getRateLimiter());
		storyFactory = testRunner.getStoryFactory(cx);
	}
	
	public TestResults run() {
		cx.getLogDir().mkdirs();
		
		// do one test as a warmup
		log.info("Running warmup test");
		TestResults testResults = new TestResults(cx);
		cx.newStory();
		Story story;
		try {
			story = storyFactory.getRateLimitedStory();
		}
		catch (Exception e) {
			log.error("Warmup test failed; no Story provided by {}.", storyFactory.getName(), e);
			return testResults;
		}
		if(story==null) {
			log.error("Warmup test failed; no story provided by {}.", storyFactory.getName());
			return testResults;			
		}
		log.debug("Running warmup story {}", story.getName());
		StoryResult storyResult;
		try {
			storyResult = story.call(cx);
		}
		catch(Throwable t) {
			log.error("Warmup test threw exception", t);
			storyResult = new StoryResult(story.getName());
			storyResult.setException(t);
			storyResult.setPassed(false);
		}
		testResults.addResult(storyResult);
		if(!storyResult.isPassed()) {
			log.error("Warmup test failed; aborting.");
			return testResults;
		}
		log.info("Warmup test passed.");
		
		// run the rest of the tests
		testResults.testStarting();
		LinkedList<TestThread> threads = new LinkedList<TestThread>();
		for(int i=0; i < numThreads; ++i) {
			threads.add(new TestThread(cx, storyFactory, testResults, numIterationsPerThread));
		}
		
		// implementer decides when the test completes
		runTests(cx, storyFactory, threads, testResults);
		
		testResults.testEnded();
		Util.writeFile(cx.getLogDir(), "index.html", testResults.toHtml());
		storyFactory.shutdown();
		cx.close();
		log.info("{} Results.", testResults.size());
		return testResults;
	}
	
	public abstract void runTests(TestContext cx, StoryFactory testFactory, List<TestThread> threads, TestResults ret);

}
