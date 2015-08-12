package net.darylb.stressor.actions;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.LoadTestContext;

public class DatabaseCloseConnectionAction extends Action {

	private static final Logger log = LoggerFactory.getLogger(DatabaseCloseConnectionAction.class);
	
	private Connection connection;

	public DatabaseCloseConnectionAction(Connection connection) {
		this.connection = connection;
	}

	@Override
	public ActionResult call(LoadTestContext cx) {
		try {
			connection.close();
		}
		catch (SQLException e) {
			log.warn("Error closing conection: {}", e);
		}
		return null;
	}

}
