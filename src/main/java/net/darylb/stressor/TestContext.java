package net.darylb.stressor;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class TestContext extends Properties {

	private static final long serialVersionUID = -1605284270262743221L;
	private final File logDir;
	private final String name;
	private ThreadLocal<HashMap<String, Object>> testProperties = new ThreadLocal<HashMap<String, Object>>();

	public TestContext(String name, String logDir) {
		this.name = name;
		this.logDir = new File(logDir);
	}

	public File getLogDir() {
		return logDir;
	}

	public String getName() {
		return name;
	}

	public void newTest() {
		testProperties.set(new HashMap<String, Object>());
	}

	public void setTestProperty(String key, String value) {
		testProperties.get().put(key, value);
	}
	
	public String getTestProperty(String key) {
		String ret = (String)testProperties.get().get(key);
		if(ret==null) {
			throw new RuntimeException("Missing test property: " + key);
		}
		return ret;
	}
	
	public void setTestObject(String key, Object value) {
		testProperties.get().put(key, value);
	}
	
	public Object getTestObject(String key) {
		Object ret = testProperties.get().get(key);
		if(ret==null) {
			throw new RuntimeException("Missing test object: " + key);
		}		
		return ret;
	}
	
	@Override
	public String getProperty(String key) {
		String ret = super.getProperty(key);
		if(ret==null) {
			throw new RuntimeException("Missing global TestContext property: " + key);
		}
		return ret;
	}

	public boolean hasTestObject(String key) {
		return testProperties.get().containsKey(key);
	}
}
