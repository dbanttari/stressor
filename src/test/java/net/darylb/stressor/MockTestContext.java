package net.darylb.stressor;

import java.sql.Connection;

public class MockTestContext extends LoadTestContext {

	private static final long serialVersionUID = -1804751449059619804L;
	private Connection connection;

	public MockTestContext() {
		this("UnitTest", "test");
	}
	
	public MockTestContext(String name, String logDir) {
		super(name, logDir);
	}
	
	public void setConnection(Connection c) {
		this.connection = c;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
}
