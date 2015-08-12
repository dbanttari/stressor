package net.darylb.stressor.actions;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import net.darylb.stressor.LoadTestContext;

public class DatabasePoolCloseAction extends Action {

	private ComboPooledDataSource pool;

	public DatabasePoolCloseAction(ComboPooledDataSource pool) {
		this.pool = pool;
	}

	@Override
	public ActionResult call(LoadTestContext cx) {
		pool.close();
		return null;
	}

}
