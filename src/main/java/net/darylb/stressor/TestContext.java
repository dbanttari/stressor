package net.darylb.stressor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestContext extends Properties {

	private static final Logger log = LoggerFactory.getLogger(TestContext.class);
	
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

	public void setStoryProperty(String key, String value) {
		testProperties.get().put(key, value);
	}
	
	public String getStoryProperty(String key) {
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
			throw new RuntimeException("Missing global stressor.property: " + key);
		}
		return ret;
	}

	public boolean hasTestObject(String key) {
		return testProperties.get().containsKey(key);
	}

	LinkedList<Connection>connections = new LinkedList<Connection>();

	private int numThreads;
	public Connection getConnection() throws SQLException {
		Connection ret;
		log.info("Connecting to {}", this.getProperty("jdbc.url"));
		if(this.containsKey("jdbc.username")) {
			ret = DriverManager.getConnection(this.getProperty("jdbc.url"), this.getProperty("jdbc.username"), this.getProperty("jdbc.password"));
		}
		else {
			ret = DriverManager.getConnection(this.getProperty("jdbc.url"));
		}
		connections.add(ret);
		return ret;
	}
	

	public void shutdown() {
		for(Connection c : connections) {
			try {
				c.close();
			}
			catch (SQLException e) {
				log.warn("Error closing connection", e);
			}
		}		
	}

	public int getNumThreads() {
		return numThreads;
	}
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
}
