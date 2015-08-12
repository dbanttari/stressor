package net.darylb.stressor.actions;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import net.darylb.stressor.LoadTestContext;

/**
 * This expects a consistent username and password per JDBC URL.  If you're testing
 * with many different username/password combinations, consider creating a different
 * version of this that doesn't use connection pooling.
 * @author daryl
 *
 */
public abstract class DatabaseAction extends Action {

	private static final String DATABASE_POOL_KEY = "DEFAULT_DB_POOL";
	private static final String DATABASE_CLEANUP_KEY = "DEFAULT_DB_CLEANUP";
	private static final String DATABASE_CONNECTION_KEY = "DATABASE_STORY_CONNECTION";
	private String jdbcUrl;
	private String username;
	private String password;
	private String driverClass;
	protected Connection c;
	
	public DatabaseAction() {
	}
	
	public DatabaseAction(String driverClass, String jdbcUrl, String username, String password) {
		this.driverClass = driverClass;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}


	@Override
	public ActionResult call(LoadTestContext cx) {
		
		if(driverClass==null) {
			driverClass = cx.getStoryProperty(Props.JDBC_DRIVER);
			jdbcUrl = cx.getStoryProperty(Props.JDBC_URL);
			if(cx.hasStoryProperty(Props.JDBC_USERNAME)) {
				username = cx.getStoryProperty(Props.JDBC_USERNAME);
				password = cx.getStoryProperty(Props.JDBC_PASSWORD);
			}
		}
		
		ActionResult ret = null;
		
		ComboPooledDataSource pool = getConnectionPool(cx);
		c = getConnection(cx, pool);
		
		try {
			ret = call(cx, c);
			cx.registerStoryCleanupAction(DATABASE_CLEANUP_KEY, new DatabaseCloseConnectionAction(c));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * get (or create) connection for Story
	 * @param cx
	 * @param pool
	 * @return
	 */
	private Connection getConnection(LoadTestContext cx, ComboPooledDataSource pool) {
		Connection c;
		if(cx.hasStoryObject(DATABASE_CONNECTION_KEY)) {
			c = (Connection)cx.getStoryObject(DATABASE_CONNECTION_KEY);
		}
		else {
			try {
				c = pool.getConnection();
			}
			catch (SQLException e) {
				throw new RuntimeException("Cannot get connection from pool", e);
			}
		}
		return c;
	}

	/**
	 * get (or create) connection pool for Test
	 * @param cx
	 * @return
	 */
	private ComboPooledDataSource getConnectionPool(LoadTestContext cx) {
		String poolKey = DATABASE_POOL_KEY + jdbcUrl;
		ComboPooledDataSource pool;
		if(cx.containsKey(poolKey)) {
			pool = (ComboPooledDataSource)cx.get(poolKey);
		}
		else {
			pool = createPool(cx.getNumThreads());
			cx.put(poolKey, pool);
			cx.registerCleanupAction(jdbcUrl, new DatabasePoolCloseAction(pool));
		}
		return pool;
	}

	public abstract ActionResult call(LoadTestContext cx, Connection connection) throws SQLException;

	private ComboPooledDataSource createPool(int numThreads) {
		ComboPooledDataSource pool = new ComboPooledDataSource();
		try {
			pool.setDriverClass(driverClass);
		}
		catch (PropertyVetoException e) {
			throw new RuntimeException("Unable to load JDBC driver", e);
		}
		pool.setJdbcUrl(jdbcUrl);
		if(username != null) {
			pool.setUser(username);
			pool.setPassword(password);
		}
		pool.setMaxPoolSize(10+numThreads*3);
		pool.setMaxStatements(10+numThreads*3);
		pool.setMaxStatementsPerConnection(10);
		pool.setMinPoolSize(numThreads);
		return pool;
	}
	
	protected ResultSet preparedQuery(String sql, Object... params) throws SQLException {
		PreparedStatement ps = c.prepareStatement(sql);
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		return ps.executeQuery();
	}

	protected int preparedUpdate(String sql, Object... params) throws SQLException {
		PreparedStatement ps = c.prepareStatement(sql);
		for(int i=0; i < params.length; ++i) {
			ps.setObject(i+1, params[i]);
		}
		int ret = ps.executeUpdate();
		ps.close();
		return ret;
	}
	
	/**
	 * The statement and resultset should be closed by the DatabaseCloseConnectionAction
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected ResultSet query(String sql) throws SQLException {
		Statement s = c.createStatement();
		ResultSet rs =  s.executeQuery(sql);
		return rs;
	}
	
	protected int update(String sql) throws SQLException {
		PreparedStatement ps = c.prepareStatement(sql);
		int ret = ps.executeUpdate();
		ps.close();
		return ret;
	}
	
}
