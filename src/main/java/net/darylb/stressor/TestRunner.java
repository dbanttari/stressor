package net.darylb.stressor;

import java.io.File;
import java.util.Calendar;

public abstract class TestRunner {
	
	public TestResults test(String directoryPrefix, TestFactory factory, int threads, int testsPerThread) throws Throwable {
		Calendar c = Calendar.getInstance();
		StringBuffer timestamp = new StringBuffer();
		timestamp
			.append(c.get(Calendar.YEAR))
			.append(nn(Calendar.MONTH))
			.append(nn(Calendar.DATE))
			.append(nn(Calendar.HOUR))
			.append(nn(Calendar.MINUTE))
			.append(nn(Calendar.SECOND));
		String name = directoryPrefix + timestamp;
		File outputDir = new File(name);
		outputDir.mkdirs();
		LoadTest loadTest = new LoadTest(name, factory, threads, testsPerThread, outputDir);
		return loadTest.doTests();
	}

	private String nn(int n) {
		if(n < 10) {
			return "0" + Integer.toString(n);
		}
		return Integer.toString(n);
	}
	
}
