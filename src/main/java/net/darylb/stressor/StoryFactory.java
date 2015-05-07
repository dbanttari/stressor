package net.darylb.stressor;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Story Factory is responsible for producing a new, configured Story object
 * each time {@link #getStory()} is called.
 * @author daryl
 * @see TestDefinition#getStoryFactory()
 */
public abstract class StoryFactory extends TestHelper {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StoryFactory.class);

	private TestContext cx;

	private String name;

	ResultSetQueue resultSetQueue;

	public StoryFactory(TestContext cx) {
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

	/**
	 * Causes this StoryFactory to iterate over the results of this query.
	 * Results are pre-fetched, so more rows may be fetched by this than
	 * are actually used by the test. If the query runs out of rows, it
	 * will be repeated if isRepeatable==true, otherwise it will return null.
	 * @param sql The query to iterate over.
	 * @see #getNextRow()
	 */
	public void useQuery(String sql, boolean isRepeatable) {
		try {
			resultSetQueue = new ResultSetQueue(cx, sql, isRepeatable, 20);
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Object[] getNextRow() {
		Object[] ret = resultSetQueue.getNextRow();
		if(ret==null) {
			throw new TestOverException();
		}
		return ret;
	}
	
	/**
	 * Implementors are expected to return a new configured Story each time this is called.
	 * @return The Story to be executed.  Each Story is run on one thread (though many stories
	 * 			may run in parallel.)
	 * @throws Exception
	 * @see Story
	 */
	public abstract Story getStory() throws Exception;

	/**
	 * Called by the load test once the last story has been retrieved using getStory(), but
	 * possibly before the load test is complete.
	 */
	protected void shutdown() {
		if(resultSetQueue != null) {
			resultSetQueue.shutdown();
		}
		cx.close();
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	public Story getRateLimitedStory() throws Exception {
		cx.limitRate();
		return getStory();
	}
	
}
