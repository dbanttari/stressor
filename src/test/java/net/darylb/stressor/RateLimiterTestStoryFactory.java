package net.darylb.stressor;

public class RateLimiterTestStoryFactory extends StoryFactory {

	public RateLimiterTestStoryFactory(TestContext cx) {
		super(cx);
	}

	@Override
	public Story getStory() throws Exception {
		return new RateLimiterTestStory();
	}

}
