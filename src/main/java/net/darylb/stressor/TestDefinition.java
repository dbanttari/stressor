package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestDefinition {
	
	public static Logger log = LoggerFactory.getLogger(TestDefinition.class);
	private String name ;

	public TestDefinition() {
		this.name = this.getClass().getSimpleName();
	}
	
	public TestDefinition(String name) {
		this.name = name;
	}
	
	public abstract StoryFactory getStoryFactory(TestContext cx);
	
	public TestContext getTestContext() {
		TestContext ret = new TestContext(name, "loadtests/" + name + "/" + Util.getTimestamp());
		Util.loadProperties(ret);
		return ret;
	}
	
}
