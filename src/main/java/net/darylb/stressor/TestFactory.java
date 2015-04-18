package net.darylb.stressor;

public interface TestFactory {

	Test getTest(TestContext cx);

	void shutdown() throws Throwable;
	
}
