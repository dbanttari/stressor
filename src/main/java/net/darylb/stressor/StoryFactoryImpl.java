package net.darylb.stressor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Story Factory is responsible for producing a new, configured Story object
 * each time {@link #getStory()} is called.
 * @author daryl
 * @see LoadTestDefinition#getStoryFactory()
 */
public abstract class StoryFactoryImpl extends LoadTestHelper implements StoryFactory {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StoryFactoryImpl.class);

	private LoadTestContext cx;

	private String name;

	ResultSetQueue resultSetQueue;

	public StoryFactoryImpl(LoadTestContext cx) {
		this.cx = cx;
		this.name = this.getClass().getSimpleName();
	}
	
	/**
	 * Causes this StoryFactory to iterate over the results of this query.
	 * Results are pre-fetched, so more rows may be fetched by this than
	 * are actually used by the test. If the query runs out of rows, it
	 * will be repeated.
	 * @param sql The query to iterate over.
	 * @see getNextRow()
	 */
	protected void useQuery(String sql) {
		useQuery(sql, true);
	}

	/* (non-Javadoc)
	 * @see net.darylb.stressor.StoryFactory#useQuery(java.lang.String)
	 */
	@Override
	public void useQuery(String sql, boolean isRepeatable) {
		try {
			resultSetQueue = new ResultSetQueue(cx, sql, isRepeatable, 20);
		}
		catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object[] getNextRow() {
		Object[] ret = resultSetQueue.getNextRow();
		if(ret==null) {
			throw new LoadTestCompleteException();
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see net.darylb.stressor.StoryFactory#getStory()
	 */
	@Override
	public abstract Story getStory() throws Exception;

	/**
	 * Called by the load test once the last story has been retrieved using getStory(), but
	 * possibly before the load test is complete.
	 */
	public void shutdown() {
		if(resultSetQueue != null) {
			resultSetQueue.shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see net.darylb.stressor.StoryFactory#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.darylb.stressor.StoryFactory#getRateLimitedStory()
	 */
	@Override
	public Story getRateLimitedStory() throws Exception {
		cx.limitRate();
		return getStory();
	}
	
}
