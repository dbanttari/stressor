package net.darylb.stressor;

import static org.junit.Assert.*;

import net.darylb.stressor.actions.Props;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetQueueTest {

	private static Logger log = LoggerFactory.getLogger(ResultSetQueueTest.class);
	
	Object[][] results = {{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12},{13},{14},{15},{16},{17},{18},{19},{20}};

	@Test
	public void testFinite() throws Exception {
		MockTestContext cx = new MockTestContext("Test","test");
		setupDatabaseMock(cx);
		
		ResultSetQueue queue = new ResultSetQueue(cx, "select 1 from blah blah blah", false, 10);
		
		int lastValue = -1;
		Object[] row;
		int count = 0;
		while( (row = queue.getNextRow()) != null ) {
			log.debug("New row: {}", row);
			assertNotEquals(lastValue, row[0]);
			count++;
		}
		assertEquals(results.length, count);
	}
	
	@Test
	public void testInfinite() throws Exception {
		MockTestContext cx = new MockTestContext("Test","test");
		setupDatabaseMock(cx);
		
		ResultSetQueue queue = new ResultSetQueue(cx, "select 1 from blah blah blah", true, 10);
		
		int lastValue = -1;
		Object[] row;
		int count = 0;
		while( (row = queue.getNextRow()) != null && count < 200) {
			log.debug("New row: {}", row);
			assertNotEquals(lastValue, row[0]);
			count++;
		}
		assertEquals(200, count);
	}

	private void setupDatabaseMock(MockTestContext cx) {
		MockResultSet rs = new MockResultSet(results);
		MockStatement s = new MockStatement(rs);
		MockConnection c = new MockConnection(s);
		MockDriver.setConnection(c);
		cx.setProperty(Props.JDBC_DRIVER, MockDriver.class.getName());
		cx.setProperty(Props.JDBC_URL, "jdbc:mock://foo");
	}

}
