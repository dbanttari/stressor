package net.darylb.stressor;

public class MockLoadTestContext extends LoadTestContext {

	private static final long serialVersionUID = -1804751449059619804L;

	public MockLoadTestContext() {
		this("UnitTest", "test");
	}
	
	public MockLoadTestContext(String name, String logDir) {
		super(name, logDir);
	}
	
}
