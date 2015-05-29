package net.darylb.stressor;

public class MockTestDefinition extends LoadTestDefinition {

	@Override
	public StoryFactory getStoryFactory(LoadTestContext cx) {
		return new MockStoryFactory(cx);
	}

}
