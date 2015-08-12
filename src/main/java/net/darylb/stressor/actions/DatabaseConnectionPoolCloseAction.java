package net.darylb.stressor.actions;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import net.darylb.stressor.LoadTestContext;

/**
 * Closes the JDBC connection pool
 */
public class DatabaseConnectionPoolCloseAction extends Action {

	private ComboPooledDataSource pool;
	public DatabaseConnectionPoolCloseAction(ComboPooledDataSource pool) {
		this.pool = pool;
	}
	
	@Override
	public ActionResult call(LoadTestContext cx) {
		pool.close();
		return null;
	}
	
}
