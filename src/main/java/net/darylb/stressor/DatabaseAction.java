package net.darylb.stressor;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseAction extends Action {

	
	@Override
	public ActionResult call(TestContext cx) {
		ActionResult ret = null;
		try {
			ret = call(cx, super.getConnection(cx));
			super.closeResultSet(cx);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public abstract ActionResult call(TestContext cx, Connection connection) throws SQLException;

}
