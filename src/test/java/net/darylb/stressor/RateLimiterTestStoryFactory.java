package net.darylb.stressor;

public class RateLimiterTestStoryFactory extends StoryFactoryImpl {

	public RateLimiterTestStoryFactory(LoadTestContext cx) {
		super(cx);
	}

	@Override
	public Story getStory() throws Exception {
		return new RateLimiterTestStory();
	}

}
