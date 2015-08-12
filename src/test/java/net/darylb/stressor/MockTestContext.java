package net.darylb.stressor;

public class MockTestContext extends LoadTestContext {

	private static final long serialVersionUID = -1804751449059619804L;

	public MockTestContext() {
		this("UnitTest", "test");
	}
	
	public MockTestContext(String name, String logDir) {
		super(name, logDir);
	}
	
}
