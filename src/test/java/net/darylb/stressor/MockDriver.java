package net.darylb.stressor;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class MockDriver implements Driver {

	static {
		try {
			DriverManager.registerDriver(new MockDriver());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static MockConnection connection;

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		return connection;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url.toLowerCase().contains("mock");
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	public static void setConnection(MockConnection c) {
		MockDriver.connection = c;
	}

}
