package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestThread implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(TestThread.class);

	private TestContext cx;
	private StoryFactory storyFactory;
	private TestResults testResults;
	private Thread thread;
	private boolean isRunning = true;
	private static int num = 1;

	private int maxIterations;

	public TestThread(TestContext cx, StoryFactory testFactory, TestResults testResults) {
		this(cx, testFactory, testResults, -1);
	}
	
	public TestThread(TestContext cx, StoryFactory testFactory, TestResults testResults, int maxIterations) {
		this.cx = cx;
		this.storyFactory = testFactory;
		this.testResults = testResults;
		this.maxIterations = maxIterations;
		this.thread = new Thread(this);
		this.thread.setName(testFactory.getName() + " TestThread " + Integer.toString(num++));
		this.thread.setDaemon(true);
		this.thread.start();
	}

	public void join(long timeout) {
		shutdown();
		try {
			this.thread.join(timeout);
		}
		catch (InterruptedException e) {
			Thread.interrupted();
		}
		this.thread.interrupt();
	}

	public void shutdown() {
		this.isRunning = false;
	}

	@Override
	public void run() {
		log.info("Thread {} starting!", thread.getName());
		while(isRunning) {
			String storyName = storyFactory.getClass().getSimpleName();
			try {
				Story story = storyFactory.getStory();
				if(story==null) {
					log.warn("Test factory {} exhausted", storyFactory.getName());
					return;
				}
				log.info("Running test {}", story.getName());
				cx.newTest();
				TestResult testResult = story.call(cx);
				testResults.addResult(testResult);
			}
			catch(TestOverException t) {
				log.info("StoryFactory ran out of stories.");
				maxIterations = 0;
			}
			catch(Throwable t) {
				log.error("Error in {}", storyName, t);
			}
			isRunning = maxIterations == -1 || --maxIterations > 0;
		}
		log.info("Thread {} complete.", thread.getName());
	}

	public void join() {
		try {
			this.thread.join();
		}
		catch (InterruptedException e) {
			log.warn("The wait for test thread completion was interrupted", e);
			Thread.interrupted();
		}
	}

}
