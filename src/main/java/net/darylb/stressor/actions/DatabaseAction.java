package net.darylb.stressor.actions;

import java.sql.Connection;
import java.sql.SQLException;

import net.darylb.stressor.TestContext;

public abstract class DatabaseAction extends Action {

	
	@Override
	public ActionResult call(TestContext cx) {
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

	public abstract ActionResult call(TestContext cx, Connection connection) throws SQLException;

}
