package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoadTest {

	private static Logger log = LoggerFactory.getLogger(LoadTest.class);
	
	private final int numThreads;
	private final TestContext cx;
	private final StoryFactory testFactory;
	private final int numIterationsPerThread;

	public LoadTest(TestDefinition testRunner, int numThreads, int numIterationsPerThread) {
		this.cx = testRunner.getTestContext();
		cx.setNumThreads(numThreads);
		if(cx.containsKey("jdbc.driver")) {
			try {
				Class.forName(cx.getProperty("jdbc.driver"));
			}
			catch (ClassNotFoundException e) {
				log.error("Could not load jdbc.driver", e);
			}
		}
		this.testFactory = testRunner.getStoryFactory(cx);
		this.numThreads = numThreads;
		this.numIterationsPerThread = numIterationsPerThread;
	}
	
	public TestResults run() {
		cx.getLogDir().mkdirs();
		
		// do one test as a warmup
		log.info("Running warmup test");
		TestResults ret = new TestResults(cx);
		cx.newTest();
		Story story;
		try {
			story = testFactory.getStory();
		}
		catch (Exception e) {
			log.error("Warmup test failed; no Story provided by {}.", testFactory.getName(), e);
			return ret;
		}
		if(story==null) {
			log.error("Warmup test failed; no story provided by {}.", testFactory.getName());
			return ret;			
		}
		log.debug("Running warmup story {}", story.getName());
		TestResult result;
		try {
			result = story.call(cx);
		}
		catch(Throwable t) {
			log.error("Warmup test threw exception", t);
			result = new TestResult(story.getName());
			result.setException(t);
			result.setPassed(false);
		}
		ret.addResult(result);
		if(!result.isPassed()) {
			log.error("Warmup test failed; aborting.");
			return ret;
		}
		log.info("Warmup test passed.");
		
		// run the rest of the tests
		ret.testStarting();
		LinkedList<TestThread> threads = new LinkedList<TestThread>();
		for(int i=0; i < numThreads; ++i) {
			threads.add(new TestThread(cx, testFactory, ret, numIterationsPerThread));
		}
		
		// implementer decides when the test completes
		runTests(cx, testFactory, threads, ret);
		
		ret.testEnded();
		Util.writeFile(cx.getLogDir(), "index.html", ret.toHtml());
		testFactory.shutdown();
		cx.shutdown();
		log.info("{} Results.", ret.size());
		return ret;
	}
	
	public abstract void runTests(TestContext cx, StoryFactory testFactory, List<TestThread> threads, TestResults ret);

}
