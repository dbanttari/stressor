package net.darylb.stressor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StoryFactory {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StoryFactory.class);

	private TestContext cx;

	private String name;

	ResultSetQueue resultSetQueue;
	
	public StoryFactory(TestContext cx) {
		this.cx = cx;
		this.name = this.getClass().getSimpleName();
	}

	protected void useQuery(String sql) throws SQLException {
		resultSetQueue = new ResultSetQueue(cx, sql, true, 20);
	}
	
	public abstract Story getStory() throws Exception;

	void shutdown() {
		if(resultSetQueue != null) {
			resultSetQueue.shutdown();
		}
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
}
