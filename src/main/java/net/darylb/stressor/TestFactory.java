package net.darylb.stressor;

public interface TestFactory {

	Test getTest();

	void shutdown() throws Throwable;
	
}
