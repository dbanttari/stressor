package net.darylb.stressor;

public class RateLimiterImpl implements RateLimiter, Runnable {

	public enum Interval {
		SECOND(1000L),
		MINUTE(1000L * 60L),
		HOUR(1000L * 60L * 60L);
		long ms;
		Interval(long ms) {
			this.ms = ms;
		}
		public long getIntervalMs() {
			return ms;
		}
	}
	
	private long minTimeMs;
	private Thread thread;
	private volatile boolean allowNext = true;
	private long nextWakeupTick;

	public RateLimiterImpl(long count, Interval interval) {
		this(interval.getIntervalMs() / count);
	}
	
	public RateLimiterImpl(long minTimeMs) {
		this.minTimeMs = minTimeMs;
		if(minTimeMs < 10) {
			throw new IllegalArgumentException("Interval too short to implement");
		}
		this.thread = new Thread(this);
		this.thread.setName("Rate Limiter (" + minTimeMs + "ms)");
		this.thread.setDaemon(true);
		this.thread.start();
	}

	public void limitRate() {
		synchronized(this) {
			if(minTimeMs <= 0) {
				return;
			}
			if(allowNext) {
				// we're below the rate limit.  Allow this thread to proceed
				allowNext = false;
				return;
			}
			else {
				try {
					// wait for rate limiter to notify us to proceed
					this.wait();
				}
				catch (InterruptedException e) {
					Thread.interrupted();
				}
				// but don't allow the next thread to proceed
				allowNext = false;
			}
		}
	}
	
	@Override
	public void run() {
		nextWakeupTick = System.currentTimeMillis() + minTimeMs;
		while(true) {
			try {
				// we have about 5ms overhead in the loop
				Thread.sleep(nextWakeupTick - System.currentTimeMillis());
			}
			catch (InterruptedException e) {
				Thread.interrupted();
			}
			nextWakeupTick += minTimeMs;
			synchronized(this) {
				allowNext = true;
				// wake up one waiting thread (if applicable)
				// if one wakes up it'll turn allowNext=false
				this.notify();
			}
		}
	}

}
