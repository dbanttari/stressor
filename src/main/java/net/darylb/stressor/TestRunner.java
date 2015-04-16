package net.darylb.stressor;

import java.util.Calendar;

public abstract class TestRunner {
	
	public TestResults test(TestContext cx, TestFactory factory, int threads, int testsPerThread) throws Throwable {
		cx.getLogDir().mkdirs();
		LoadTest loadTest = new LoadTest(cx, factory, threads, testsPerThread);
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
	
}
