package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoadTest {

	private static Logger log = LoggerFactory.getLogger(LoadTest.class);
	
	private final int numThreads;
	private final LoadTestContext cx;
	private final StoryFactory storyFactory;
	private final long startTime;

	private LoadTestResults testResults;
	private LoadTestStatus status = LoadTestStatus.PENDING;

	public LoadTest(LoadTestContext cx, StoryFactory storyFactory, int numThreads) {
		this.cx = cx;
		this.numThreads = numThreads;
		cx.setNumThreads(numThreads);
		this.startTime = System.currentTimeMillis() / 1000L;
		this.storyFactory = storyFactory;
		cx.setNumThreads(numThreads);
	}
	
	public LoadTestResults run() {
		this.status = LoadTestStatus.VALIDATING;
		try {
			// do one test as a warmup
			log.info("Running warmup test");
			testResults = new LoadTestResults(cx);
			cx.newStory();
			Story story;
			try {
				story = storyFactory.getRateLimitedStory();
			}
			catch (Exception e) {
				log.error("Warmup test failed; no Story provided by {}.", storyFactory.getName(), e);
				this.status = LoadTestStatus.FAILED;
				return testResults;
			}
			if(story==null) {
				log.error("Warmup test failed; no story provided by {}.", storyFactory.getName());
				this.status = LoadTestStatus.FAILED;
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
				this.status = LoadTestStatus.FAILED;
				return testResults;
			}
			log.info("Warmup test passed.");
			this.status = LoadTestStatus.RUNNING;
			
			// run the rest of the tests
			testResults.testStarting();
			LinkedList<LoadTestThread> threads = new LinkedList<LoadTestThread>();
			for(int i=0; i < numThreads; ++i) {
				threads.add(new LoadTestThread(cx, storyFactory, testResults));
			}
			
			// implementer decides when the test completes
			runTests(cx, storyFactory, threads, testResults);
			
			testResults.testEnded();
			cx.logFile("index.html", testResults.toHtml());
			storyFactory.shutdown();
			cx.close();
			log.info("{} Results.", testResults.size());
		}
		catch(RuntimeException e) {
			this.status = LoadTestStatus.FAILED;
			throw e;
		}
		this.status = LoadTestStatus.COMPLETE;
		return testResults;
	}
	
	public abstract void runTests(LoadTestContext cx, StoryFactory testFactory, List<LoadTestThread> threads, LoadTestResults ret);

	public long getStartTime() {
		return startTime;
	}
	
	public LoadTestResults getTestResults() {
		return testResults;
	}
	
	public abstract double getProgressPct();

	public StoryFactory getStoryFactory() {
		return storyFactory;
	}
	
	public LoadTestStatus getStatus() {
		return status;
	}

	public LoadTestContext getLoadTestContext() {
		return cx;
	}

}
