package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTestThread implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(LoadTestThread.class);

	private LoadTestContext cx;
	private StoryFactory storyFactory;
	private LoadTestResults testResults;
	private Thread thread;
	private boolean isRunning = true;
	private static int num = 1;

	public LoadTestThread(LoadTestContext cx, StoryFactory testFactory, LoadTestResults testResults) {
		this.cx = cx;
		this.storyFactory = testFactory;
		this.testResults = testResults;
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
		String storyFactoryName = storyFactory.getClass().getSimpleName();
		while(isRunning) {
			try {
				Story story = storyFactory.getRateLimitedStory();
				if(story==null) {
					log.warn("Test factory {} exhausted", storyFactory.getName());
					isRunning=false;
					return;
				}
				log.debug("Running test {}", story.getName());
				cx.newStory();
				StoryResult testResult = story.call(cx);
				testResults.addResult(testResult);
			}
			catch(LoadTestCompleteException t) {
				isRunning = false;
			}
			catch(Throwable t) {
				log.error("Error in {}", storyFactoryName, t);
			}
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
