package net.darylb.stressor;

import java.util.Calendar;

public abstract class TestRunner {
	
	public TestResults doFixedTest(int threads, int testsPerThread) {
		TestContext cx = getTestContext();
		TestFactory fac = getTestFactory(cx);
		cx.getLogDir().mkdirs();
		FixedLoadTest loadTest = new FixedLoadTest(cx, fac, threads, testsPerThread);
		return loadTest.doTests();
	}
	
	public TestResults doTimedTest(int threads, long endTick) {
		TestContext cx = getTestContext();
		TestFactory fac = getTestFactory(cx);
		cx.getLogDir().mkdirs();
		TimedLoadTest loadTest = new TimedLoadTest(cx, fac, threads, endTick);
		return loadTest.doTests();
	}
	
	public static String getTimestamp() {
		Calendar c = Calendar.getInstance();
		StringBuffer timestamp = new StringBuffer();
		timestamp
			.append(c.get(Calendar.YEAR))
			.append(nn(c.get(Calendar.MONTH)))
			.append(nn(c.get(Calendar.DATE)))
			.append(nn(c.get(Calendar.HOUR)))
			.append(nn(c.get(Calendar.MINUTE)))
			.append(nn(c.get(Calendar.SECOND)));
		return timestamp.toString();
	}
	private static String nn(int n) {
		if(n < 10) {
			return "0" + Integer.toString(n);
		}
		return Integer.toString(n);
	}
	
	public abstract TestFactory getTestFactory(TestContext cx);
	
	public abstract TestContext getTestContext();
	
}
