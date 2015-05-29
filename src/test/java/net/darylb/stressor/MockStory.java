package net.darylb.stressor;

import net.darylb.stressor.actions.MockAction;

public class MockStory extends Story {

	public MockStory() {
		this.addAction(new MockAction());
	}
	
}
