package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When running a load test, this is the definition of what a load test
 * should do.  In most cases, simply implementing {@link #getStoryFactory(LoadTestContext)}
 * will suffice, but overriding {@link #getLoadTestContext()} can be useful if
 * parameters are retrieved from other than stressor.properties.
 * @author daryl
 *
 */
public abstract class LoadTestDefinition {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoadTestDefinition.class);
	private String name ;

	public LoadTestDefinition() {
		this.name = this.getClass().getSimpleName();
	}
	
	public LoadTestDefinition(String name) {
		this.name = name;
	}
	
	/**
	 * Return a {@link StoryFactoryImpl} for this test.
	 * @param cx
	 * @return the {@link StoryFactoryImpl} implementation for this test
	 */
	public abstract StoryFactory getStoryFactory(LoadTestContext cx);
	
	/**
	 * Returns a configured TestContext object (which, by default, will load global
	 * properties from stressor.properties in the current directory.)  Implementors
	 * may override this if simply reading properties from stressor.properties will
	 * not suffice.
	 * @return a configured TestContext
	 */
	public LoadTestContext getLoadTestContext() {
		return new LoadTestContext(name, "loadtests/" + name + "/" + Util.getTimestamp());
	}

	/**
	 * By default, tests are not rate limited.  To set a rate limit, return a
	 * RateLimiter that will limit the rate as needed.
	 * @return a RateLimiter configured to your needs
	 * @see #RateLimiterImpl
	 */
	public RateLimiter getRateLimiter() {
		// by default, use no rate limit. 
		return null;
	}
	
}
