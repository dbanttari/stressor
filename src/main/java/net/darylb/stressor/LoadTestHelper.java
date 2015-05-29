package net.darylb.stressor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTestHelper {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoadTestHelper.class);
	
	private Connection c;
	
	private ResultSet rs;

	private Statement s;

	protected void closeResultSet(LoadTestContext cx) {
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
		try {
			if(c==null) {
				c = cx.getStoryConnection();
			}
			else
				if (c.isClosed()) {
					c = cx.getStoryConnection();
				}
			}
		catch (SQLException e) {
			// <shrug>
			e.printStackTrace();
		}
	}
	
	protected ResultSet preparedQuery(LoadTestContext cx, String sql, Object... params) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		return ps.executeQuery();
	}

	protected int preparedUpdate(LoadTestContext cx, String sql, Object... params) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		int ret = ps.executeUpdate();
		return ret;
	}
	
	protected ResultSet query(LoadTestContext cx, String sql) throws SQLException {
		closeResultSet(cx);
		s = c.createStatement();
		rs =  s.executeQuery(sql);
		return rs;
	}
	
	protected int update(LoadTestContext cx, String sql) throws SQLException {
		closeResultSet(cx);
		PreparedStatement ps = c.prepareStatement(sql);
		s = ps;
		int ret = ps.executeUpdate();
		return ret;
	}
	
	public ActionResult runAction(LoadTestContext cx, Action action) {
		ActionResult ret = action.call(cx);
		if(ret != null) {
			try {
				action.validate(cx, ret.getContent());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
	
	public void sleep(long ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			// clear interrupted flag
			Thread.interrupted();
		}
	}
	
}
