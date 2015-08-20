package net.darylb.stressor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import net.darylb.stressor.actions.Action;
import net.darylb.stressor.switchboard.Switchboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LoadTestContext extends Properties {

	private static final Logger log = LoggerFactory.getLogger(LoadTestContext.class);
	
	private static final long serialVersionUID = -1605284270262743221L;
	private FileLogger fileLogger;
	private final String name;
	private ThreadLocal<HashMap<String, Object>> storyProperties = new ThreadLocal<HashMap<String, Object>>() {
		@Override
		protected HashMap<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};
	
	private final Switchboard switchboard = Switchboard.getInstance();

	public LoadTestContext(String name, String logDir) {
		this(name);
		this.fileLogger = new FilesystemFileLogger(logDir);
		
	}
	public LoadTestContext(String name) {
		this.name = name;
		Util.loadProperties(this);
	}
	
	private int numThreads;
	public int getNumThreads() {
		return numThreads;
	}
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public String getName() {
		return name;
	}

	/**
	 * Called at the start of every test; will close any connections
	 * left over from the previous test that used this thread
	 */
	public void newStory() {
		// make sure the new story gets new story properties and cleanup actions every time
		storyProperties.set(new HashMap<String,Object>());
		storyCleanupActions.set(new LinkedHashMap<String, Action>());
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
	 * a previous Action in the current Story.  If that's not found, this will look for a global
	 * property with the same name.  Will throw a RuntimeException if the property
	 * was not already set (as opposed to the default Properties behavior of returning null)
	 * @param key The name of the property to retrieve
	 * @see #setStoryProperty(String,String)
	 */
	public String getStoryProperty(String key) {
		String ret = (String)storyProperties.get().get(key);
		if(ret==null) {
			// if we can't find the property in StoryProperties, try global properties
			ret = getProperty(key);
			if(ret==null) {
				throw new RuntimeException("Missing test property: " + key);
			}
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
	 * a previous Action in the current Story.  If that's not found, this will look for a global
	 * property with the same name.  Will throw a RuntimeException if the object
	 * was not already set (as opposed to the default Map behavior of returning null)
	 * @param key The name of the object to retrieve
	 * @see #setStoryObject(String,Object)
	 */
	public Object getStoryObject(String key) {
		Object ret = storyProperties.get().get(key);
		if(ret==null) {
			ret = this.get(key);
			if(ret==null) {
				throw new RuntimeException("Missing test object: " + key);
			}
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
		return storyProperties.get().containsKey(key) || this.containsKey(key);
	}
	/**
	 * determine if a given Story property/object exists
	 * @param key
	 * @return
	 */
	public boolean hasStoryProperty(String key) {
		return storyProperties.get().containsKey(key) || this.containsKey(key);
	}

	/****  Rate Limiting ****/
	private RateLimiter rateLimiter;
	public void setRateLimiter(RateLimiter rateLimiter) {
		if(rateLimiter != null) {
			log.info("Rate limit set via {}", rateLimiter.getClass().getSimpleName());
			this.rateLimiter = rateLimiter;
		}
	}
	public RateLimiter getRateLimiter() {
		return rateLimiter;
	}
	public void limitRate() {
		if(rateLimiter != null) {
			rateLimiter.limitRate();
		}
	}
	
	/**** attaching files to load test ****/
	public void logFile(String fileName, String content) {
		fileLogger.logFile(fileName, content);
	}
	public void setFileLogger(FileLogger fileLogger) {
		this.fileLogger = fileLogger;
	}
	public FileLogger getFileLogger() {
		return this.fileLogger;
	}
	public Switchboard getSwitchboard() {
		return switchboard;
	}
	
	/**** Auto Cleanup ****/
	private ThreadLocal<LinkedHashMap<String, Action>> storyCleanupActions = new ThreadLocal<LinkedHashMap<String, Action>>();
	/**
	 * If you have an action that needs an end-of-story "finally" action to clean up, you
	 * can register the action here, and they will be run (in order of appearance) at the
	 * end of the story.  The unique key is used to ensure only one shutdown action of each
	 * type is run; for example, if a user runs three actions against the same MongoDB
	 * database, you can use a unique key for the connection that's used for all three,
	 * so it only gets closed once.
	 * @param uniqueKey
	 * @param cleanupAction
	 */
	public void registerStoryCleanupAction(String uniqueKey, Action cleanupAction) {
		storyCleanupActions.get().put(uniqueKey, cleanupAction);
	}
	public Collection<Action> getStoryCleanupActions() {
		return storyCleanupActions.get().values();
	}
	
	private LinkedHashMap<String, Action> cleanupActions = new LinkedHashMap<String, Action>();
	/**
	 * If you have an action that needs an end-of-test "finally" action to clean up, you
	 * can register the action here, and they will be run (in order of appearance) at the
	 * end of the test.  The unique key is used to ensure only one shutdown action of each
	 * type is run; see DatabaseAction for an example.
	 * @param uniqueKey
	 * @param cleanupAction
	 */
	public void registerCleanupAction(String uniqueKey, Action cleanupAction) {
		cleanupActions.put(uniqueKey, cleanupAction);
	}
	public Collection<Action> getCleanupActions() {
		return cleanupActions.values();
	}

}
