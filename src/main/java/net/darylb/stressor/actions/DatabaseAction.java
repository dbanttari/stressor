package net.darylb.stressor.actions;

import java.sql.Connection;
import java.sql.SQLException;

import net.darylb.stressor.LoadTestContext;

public abstract class DatabaseAction extends Action {

	
	@Override
	public ActionResult call(LoadTestContext cx) {
		ActionResult ret = null;
		try {
			Connection c = cx.getStoryConnection();
			ret = call(cx, c);
			c.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public abstract ActionResult call(LoadTestContext cx, Connection connection) throws SQLException;

}
