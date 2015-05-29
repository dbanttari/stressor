package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a decorator that will limit the number of stories produced by the wrapped StoryFactory
 * @author daryl
 *
 */
public class FixedLoadTestStoryFactory implements StoryFactory {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(FixedLoadTestStoryFactory.class);
	
	private volatile int totalIterations;
	private volatile int remainingIterations;
	private final StoryFactory storyFactory;
	
	/**
	 * This is a decorator that will limit the number of stories produced by the wrapped StoryFactory
	 * @param storyFactory the StoryFactory to limit
	 * @param iterations the number of iterations to limit it to producing
	 */
	public FixedLoadTestStoryFactory(StoryFactory storyFactory, int iterations) {
		this.storyFactory = storyFactory;
		this.totalIterations = iterations;
		this.remainingIterations = iterations;
	}

	@Override
	public Story getStory() throws Exception {
		return storyFactory.getStory();
	}

	@Override
	public void useQuery(String sql, boolean isRepeatable) {
		storyFactory.useQuery(sql, isRepeatable);
	}

	@Override
	public String getName() {
		return storyFactory.getName();
	}

	@Override
	public Story getRateLimitedStory() throws Exception {
		//log.debug("remaining iterations: {}", remainingIterations);
		if(remainingIterations-- <= 0) {
			return null;
		}
		else {
			return storyFactory.getRateLimitedStory();
		}
	}

	@Override
	public void shutdown() {
		storyFactory.shutdown();
	}

	public double getProgressPct() {
		double ret = Math.min(100.0, 100.0 * ( ((double)totalIterations-(double)remainingIterations) / (double)totalIterations));
		//log.debug("total {} remaining {} pct {}", totalIterations, remainingIterations, ret);
		return ret;
	}

}
