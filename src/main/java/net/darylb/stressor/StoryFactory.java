package net.darylb.stressor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StoryFactory extends DatabaseHelper {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StoryFactory.class);

	private TestContext cx;

	private String name;

	ResultSetQueue resultSetQueue;
	
	ThreadLocalConnectionPool pool;
	
	public StoryFactory(TestContext cx) {
		this.cx = cx;
		this.name = this.getClass().getSimpleName();
		pool = new ThreadLocalConnectionPool(cx);
	}
	
	protected void useQuery(String sql) {
		useQuery(sql, true);
	}

	public void useQuery(String sql, boolean isRepeatable) {
		try {
			resultSetQueue = new ResultSetQueue(cx, sql, true, 20);
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Object[] getNextRow() {
		return resultSetQueue.getNextRow();
	}
	
	public abstract Story getStory() throws Exception;

	void shutdown() {
		if(resultSetQueue != null) {
			resultSetQueue.shutdown();
		}
		pool.shutdown();
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	
}
