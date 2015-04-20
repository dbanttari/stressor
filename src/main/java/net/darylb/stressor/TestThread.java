package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestThread implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(TestThread.class);

	private TestContext cx;
	private TestFactory testFactory;
	private TestResults testResults;
	private Thread thread;
	private boolean isRunning = true;
	private static int num = 1;

	public TestThread(TestContext cx, TestFactory testFactory, TestResults testResults) {
		this.cx = cx;
		this.testFactory = testFactory;
		this.testResults = testResults;
		this.thread = new Thread(this);
		this.thread.setName(testFactory.getClass().getSimpleName() + " TestThread " + Integer.toString(num++));
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
		log.info("Thread " + thread.getName() + " starting!");
		while(isRunning) {
			String testName = testFactory.getClass().getSimpleName();
			try {
				Test test = testFactory.getTest(cx);
				testName = test.getClass().getSimpleName();
				log.info("Running test" + testName);
				TestResult testResult = test.call();
				testResults.addResult(testResult);
			}
			catch(Throwable t) {
				log.error("Error in " + testName, t);
			}
		}
		log.info("Thread " + thread.getName() + " complete.");
	}


}
