package net.darylb.stressor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetQueue implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(ResultSetQueue.class);

	private Connection c;
	private Statement s;
	private ResultSet rs;
	private String sql;
	private boolean isQueryRepeatable;
	private TestContext cx;
	private int columnCount;
	private int backlog;
	private boolean isRunning = true;
	ConcurrentLinkedQueue<Object[]> queuedResults = new ConcurrentLinkedQueue<Object[]>();

	private Thread thread;

	public ResultSetQueue(TestContext cx, String sql, boolean isQueryRepeatable, int backlog) throws SQLException {
		this.cx = cx;
		this.sql = sql;
		this.isQueryRepeatable = isQueryRepeatable;
		this.backlog = backlog;
		connect();
		thread = new Thread(this);
		thread.setName("ResultSetQueue");
		thread.setDaemon(true);
		thread.start();
	}

	void connect() throws SQLException {
		c = cx.getConnection();
		s = c.createStatement();
		rs = s.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		columnCount = rsmd.getColumnCount();
	}

	public Object[] getNextRow() {
		Object[] ret = null;
		long startTick = System.currentTimeMillis();
		while (ret == null) {
			ret = queuedResults.poll();
			if (ret == null) {
				if (!isRunning) {
					return null;
				}
				synchronized (queuedResults) {
					try {
						queuedResults.wait(1000);
					}
					catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
		}
		long time = System.currentTimeMillis() - startTick;
		if(time > 100) {
			log.warn("Took {}ms to fetch next row!", time);
		}
		return ret;
	}

	private Object[] toArray(ResultSet rs) throws SQLException {
		Object[] ret = new Object[columnCount];
		for (int i = 0; i < columnCount; i++) {
			ret[i] = rs.getObject(i + 1);
		}
		return ret;
	}

	@Override
	public void run() {
		while (isRunning) {
			while (queuedResults.size() < backlog) {
				try {
					if (rs.next()) {
						queuedResults.add(toArray(rs));
					}
					else if (isQueryRepeatable) {
						rs.close();
						rs = s.executeQuery(sql);
						if (!rs.next()) {
							isRunning = false;
						}
						else {
							queuedResults.add(toArray(rs));
						}
					}
					else {
						isRunning = false;
					}
				}
				catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

		}
	}

	public void shutdown() {
		try {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		catch (SQLException e) {
			log.warn("Error closing StoryFactory connection", e);
		}
	}

}
