package net.darylb.stressor;

public interface RateLimiter {

	/**
	 * this should block until the next request is allowed
	 */
	void limitRate();
	
}
