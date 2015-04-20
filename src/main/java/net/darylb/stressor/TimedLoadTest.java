package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedLoadTest {

	Logger log = LoggerFactory.getLogger(TimedLoadTest.class);
	
	private final long endTick;
	private final TestFactory testFactory;
	private final TestContext cx;

	private int numThreads;
	
	public TimedLoadTest(TestContext cx, TestFactory testFactory, int numThreads, long endTick) {
		this.cx = cx;
		this.testFactory = testFactory;
		this.numThreads = numThreads;
		this.endTick = endTick;
	}
	
	public TestResults doTests() {
		// do one test as a warmup
		testFactory.getTest(cx).call();
		List<Future<TestResult>> testResults = new LinkedList<Future<TestResult>>();
		TestResults ret = new TestResults(cx);
		
		List<TestThread>threads = new LinkedList<TestThread>();
		ret.testStarting();
		for(int i=0; i < numThreads; ++i) {
			threads.add(new TestThread(cx, testFactory, ret));
		}
		while(System.currentTimeMillis() < endTick) {
			try {
				Thread.sleep(endTick - System.currentTimeMillis());
			}
			catch (InterruptedException e) {
				Thread.interrupted();
			}
		}
		for(TestThread thread : threads) {
			thread.shutdown();
		}
		for(TestThread thread : threads) {
			thread.join(60000);
		}
		ret.testEnded();
		int n = 0;
		for(Future<TestResult> result : testResults) {
			TestResult thisResult;
			try {
				thisResult = result.get();
				//System.out.println(thisResult);
				Util.writeFile(cx.getLogDir(), "result" + Integer.toString(n++) + ".txt", thisResult.toString());
				ret.addResult(thisResult);
			}
			catch (InterruptedException e) {
				log.error("Test interrupted", e);
			}
			catch (ExecutionException e) {
				log.error("Test error", e);
			}
		}
		Util.writeFile(cx.getLogDir(), "index.html", ret.toHtml());
		testFactory.shutdown();
		log.info("{} Results.", testResults.size());
		return ret;
	}
	
}
