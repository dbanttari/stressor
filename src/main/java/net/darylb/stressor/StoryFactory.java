package net.darylb.stressor;

public interface StoryFactory {

	/**
	 * Causes this StoryFactory to iterate over the results of this query.
	 * Results are pre-fetched, so more rows may be fetched by this than
	 * are actually used by the test. If the query runs out of rows, it
	 * will be repeated if isRepeatable==true, otherwise it will return null.
	 * @param sql The query to iterate over.
	 * @see #getNextRow()
	 */
	void useQuery(String sql, boolean isRepeatable);

	Object[] getNextRow();

	/**
	 * Implementors are expected to return a new configured Story each time this is called.
	 * @return The Story to be executed.  Each Story is run on one thread (though many stories
	 * 			may run in parallel.)
	 * @throws Exception
	 * @see Story
	 */
	Story getStory() throws Exception;

	String getName();
	
	Story getRateLimitedStory() throws Exception;

	void shutdown();

}
