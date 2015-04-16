package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTest {

	Logger log = LoggerFactory.getLogger(LoadTest.class);
	
	private ExecutorService exec;
	private final int numIterationsPerThread;
	private final TestFactory testFactory;
	private final int numThreads;
	private final TestContext cx;
	
	public LoadTest(TestContext cx, TestFactory testFactory, int numThreads, int numIterationsPerThread) {
		this.cx = cx;
		this.testFactory = testFactory;
		this.numThreads = numThreads;
		this.numIterationsPerThread = numIterationsPerThread;
		exec = Executors.newFixedThreadPool(numThreads);
	}
	
	public TestResults doTests() throws Throwable {
		List<Test> tests = new LinkedList<Test>(); 
		List<Future<TestResult>> testResults = new LinkedList<Future<TestResult>>();
		for(int i=0; i<numIterationsPerThread * numThreads; i++) {
			Test test = testFactory.getTest();
			if(test != null) {
				tests.add(test);
			}
		}
		TestResults ret = new TestResults(cx);
		ret.testStarting();
		testResults = exec.invokeAll(tests);
		exec.shutdown();
		boolean graceful = exec.awaitTermination(1, TimeUnit.MINUTES);
		Thread.sleep(1000);
		if(!graceful) {
			log.warn("Non-graceful shutdown.");
		}
		ret.testEnded();
		int n = 0;
		for(Future<TestResult> result : testResults) {
			TestResult thisResult = result.get();
			//System.out.println(thisResult);
			Util.writeFile(cx.getLogDir(), "result" + Integer.toString(n++) + ".txt", thisResult.toString());
			ret.addResult(thisResult);
		}
		Util.writeFile(cx.getLogDir(), "index.html", ret.toHtml());
		testFactory.shutdown();
		log.info("{} Results.", testResults.size());
		return ret;
	}
	
}
