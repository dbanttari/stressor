package net.darylb.stressor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
	
	static ThreadLocalConnectionPool pool;
	
	private Connection c;
	
	private ResultSet rs;

	private Statement s;

	protected Connection getConnection(TestContext cx) {
		if(pool==null) {
			pool = new ThreadLocalConnectionPool(cx);
		}
		return pool.get();
	}

	protected void closeResultSet(TestContext cx) {
		try {
			if(rs != null && !rs.isClosed()) {
				rs.close();
			}
		}
		catch (SQLException e) {
			// <shrug>
			e.printStackTrace();
		}
		try {
			if(s != null && !s.isClosed()) {
				s.close();
			}
		}
		catch (SQLException e) {
			// <shrug>
			e.printStackTrace();
		}
		// make sure we have a valid connection for the next iteration
		if(c==null) {
			c = getConnection(cx);
		}
		else
			try {
				if (c.isClosed()) {
					c = getConnection(cx);
				}
			}
			catch (SQLException e) {
				c = getConnection(cx);
			}
		
	}
	
	protected ResultSet preparedQuery(TestContext cx, String sql, Object... params) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		return ps.executeQuery();
	}

	protected int preparedUpdate(TestContext cx, String sql, Object... params) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		int ret = ps.executeUpdate();
		return ret;
	}
	
	protected ResultSet query(TestContext cx, String sql) throws SQLException {
		closeResultSet(cx);
		s = c.createStatement();
		rs =  s.executeQuery(sql);
		return rs;
	}
	
	protected int update(TestContext cx, String sql) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		int ret = ps.executeUpdate();
		return ret;
	}
	
}
