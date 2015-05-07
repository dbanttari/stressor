package net.darylb.stressor;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is responsible for holding global properties (it extends Properties, and will automatically
 * load properties from stressor.properties in the current directory, if available.)
 * If stressor.properties includes jdbc.driver, jdbc.url, and optionally jdbc.username plus jdbc.password,
 * TestContext will also manage database connections that must be manually closed ({@link #getConnection()}
 * or that will be automatically closed at the end of a story {@link #getStoryConnection()} 
 * 
 * In addition, values can be passed between actions for individual Stories
 * using {@link #setStoryProperty(String,String)} / {@link #getStoryProperty(String)}
 * and {@link #setStoryObject(String, Object)} / {@link #getStoryObject(String)}
 * @author daryl
 *
 */
public class TestContext extends Properties {

	private static final Logger log = LoggerFactory.getLogger(TestContext.class);
	
	private static final long serialVersionUID = -1605284270262743221L;
	private final File logDir;
	private final String name;
	private ThreadLocal<HashMap<String, Object>> storyProperties = new ThreadLocal<HashMap<String, Object>>() {
		@Override
		protected HashMap<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	ComboPooledDataSource pool;
	
	public TestContext(String name, String logDir) {
		this.name = name;
		this.logDir = new File(logDir);
		Util.loadProperties(this);
	}
	
	private int numThreads;
	public int getNumThreads() {
		return numThreads;
	}
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	
	
	public File getLogDir() {
		return logDir;
	}

	public String getName() {
		return name;
	}

	/**
	 * Called at the start of every test; will close any connections
	 * left over from the previous test that used this thread
	 */
	public void newTest() {
		LinkedList<Connection> _storyConnections = storyConnections.get();
		for(Connection c : _storyConnections) {
			try {
				c.close();
			}
			catch (SQLException e) {
				log.warn("Failed to close story connection", e);
			}
		}
		_storyConnections.clear();
	}

	/**
	 * Sets a Property for the current test story.  Can be read by other Actions in the Story.
	 * @param key The name of the property
	 * @param value The value of the property
	 * @see #getStoryProperty(String)
	 */
	public void setStoryProperty(String key, String value) {
		storyProperties.get().put(key, value);
	}
	
	/**
	 * Retrieves a Property for the current test story, which is presumed to have been set by
	 * a previous Action in the current Story.  Will throw a RuntimeException if the property
	 * was not already set (as opposed to the default Properties behavior of returning null)
	 * @param key The name of the property to retrieve
	 * @see #setStoryProperty(String,String)
	 */
	public String getStoryProperty(String key) {
		String ret = (String)storyProperties.get().get(key);
		if(ret==null) {
			throw new RuntimeException("Missing test property: " + key);
		}
		return ret;
	}
	
	/**
	 * Sets an Object for the current test story.  Can be retrieved by other Actions in the Story.
	 * @param key The name of the object
	 * @param value The object
	 * @see #getStoryObject(String)
	 */
	public void setStoryObject(String key, Object value) {
		storyProperties.get().put(key, value);
	}
	
	/**
	 * Retrieves an Object for the current test story, which is presumed to have been set by
	 * a previous Action in the current Story.  Will throw a RuntimeException if the object
	 * was not already set (as opposed to the default Map behavior of returning null)
	 * @param key The name of the object to retrieve
	 * @see #setStoryObject(String,Object)
	 */
	public Object getStoryObject(String key) {
		Object ret = storyProperties.get().get(key);
		if(ret==null) {
			throw new RuntimeException("Missing test object: " + key);
		}		
		return ret;
	}
	
	/**
	 * Retrieves a global Property for the current load test, usually loaded from stressor.properties.
	 * Will throw a RuntimeException if no property by that name exists 
	 * (as opposed to the default Properties behavior of returning null)
	 * @param key The name of the property to retrieve
	 * @see #setProperty(String,String)
	 */
	@Override
	public String getProperty(String key) {
		String ret = super.getProperty(key);
		if(ret==null) {
			throw new RuntimeException("Missing global stressor.property: " + key);
		}
		return ret;
	}

	/**
	 * determine if a given Story property/object exists
	 * @param key
	 * @return
	 */
	public boolean hasStoryObject(String key) {
		return storyProperties.get().containsKey(key);
	}

	/*****   Database Connection Management *****/
	
	ThreadLocal<LinkedList<Connection>>storyConnections = new ThreadLocal<LinkedList<Connection>>() {
		@Override
		protected LinkedList<Connection> initialValue() {
			return new LinkedList<Connection>();
		}
	};
	
	private void createPool() {
		if(this.containsKey("jdbc.driver")) {
			log.info("Initializing {} connection pool", this.getProperty("jdbc.driver"));
			pool = new ComboPooledDataSource();
			try {
				pool.setDriverClass(this.getProperty("jdbc.driver"));
			}
			catch (PropertyVetoException e) {
				throw new RuntimeException(e);
			}
			pool.setJdbcUrl(this.getProperty("jdbc.url"));
			if(this.containsKey("jdbc.username")) {
				pool.setUser(this.getProperty("jdbc.username"));
				pool.setPassword(this.getProperty("jdbc.password"));
			}
			int numThreads = getNumThreads();
			log.info("Num threads: {}", numThreads);
			pool.setMaxPoolSize(10+numThreads*3);
			pool.setMaxStatements(10+numThreads*3);
			pool.setMaxStatementsPerConnection(10);
			pool.setMinPoolSize(numThreads);
		}
	}

	/**
	 * Returns a connection from the JDBC connection pool.  The caller is expected to eventually call {@link Connection#close}
	 * @return a JDBC connection defined by jdbc.driver plus jdbc.url in stressor.properties
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		if(pool==null) {
			createPool();
		}
		Connection ret = pool.getConnection();
		return ret;
	}

	/**
	 * Returns a connection from the JDBC connection pool.  The connection will automatically be closed before the next Story
	 * is run on the current thread.
	 * @return a JDBC connection defined by jdbc.driver plus jdbc.url in stressor.properties
	 * @throws SQLException
	 */
	public Connection getStoryConnection() throws SQLException {
		if(pool==null) {
			createPool();
		}
		Connection ret = pool.getConnection();
		storyConnections.get().add(ret);
		return ret;
	}

	/**
	 * Closes the JDBC connection pool, if defined.
	 */
	public void close() {
		if(pool != null) {
			pool.close();
		}
	}
	
	/****  Rate Limiting ****/
	private RateLimiter rateLimiter;
	protected void setRateLimiter(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	public void limitRate() {
		if(rateLimiter != null) {
			rateLimiter.limitRate();
		}
	}
}
