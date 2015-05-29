package net.darylb.stressor;

public class MockStoryFactory extends StoryFactoryImpl {

	public MockStoryFactory(LoadTestContext cx) {
		super(cx);
	}

	@Override
	public Story getStory() throws Exception {
		return new MockStory();
	}
	
}
