package net.darylb.stressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestDefinition {
	
	public static Logger log = LoggerFactory.getLogger(TestDefinition.class);
	private String name ;

	public TestDefinition() {
		this.name = this.getClass().getSimpleName();
	}
	
	public TestDefinition(String name) {
		this.name = name;
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
	
	public abstract StoryFactory getStoryFactory(TestContext cx);
	
	public TestContext getTestContext() {
		TestContext ret = new TestContext(name, "loadtests/" + name + "/" + getTimestamp());
		File f = new File("stressor.properties");
		if(f.exists()) {
			InputStream in;
			try {
				in = new FileInputStream(f);
				InputStreamReader reader = new InputStreamReader(in);
				ret.load(reader);
				in.close();
			}
			catch (IOException e) {
				log.error("Problem readin stressor.properties", e);
			}
		}
		else {
			log.info("stressor.properties not found in working directory.");
		}
		return ret;
	}
	
}
