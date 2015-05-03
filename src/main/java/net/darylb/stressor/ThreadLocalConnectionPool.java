package net.darylb.stressor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class ThreadLocalConnectionPool extends ThreadLocal<Connection> {

	private TestContext cx;
	// track all connections so they can be closed
	private LinkedList<Connection> connections = new LinkedList<Connection>();

	ThreadLocalConnectionPool(TestContext cx) {
		this.cx = cx;
	}
	
	@Override
	protected Connection initialValue() {
		try {
			return cx.getConnection();
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	void shutdown() {
		for(Connection c : this.connections) {
			try {
				c.close();
			}
			catch (SQLException e) {
				// ignore
			}
		}
	}
	
}
