package net.darylb.stressor;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.darylb.stressor.actions.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResults {

	private Logger log = LoggerFactory.getLogger(TestResults.class);
	
	private HashMap<Integer, Integer> resultsByStatusCode = new HashMap<Integer, Integer>();
	private AverageByLabel durationByAction = new AverageByLabel();
	private long endTick;
	private long startTick;
	private long resultCount = 0;
	private long nullResultCount = 0;
	private long passedCount = 0;
	private long failedCount = 0;
	private long actionCount = 0;
	private long totalActionDuration = 0;
	private long requestCount = 0;
	private long totalRequestDuration = 0;
	private LinkedList<String> errorPages = new LinkedList<String>();
	private final String CRLF = System.getProperty("line.separator");

	private TestContext cx;

	public TestResults(TestContext cx) {
		this.cx = cx;
	}

	synchronized public void addResult(StoryResult testResult) {
		if (testResult == null) {
			nullResultCount++;
			System.out.println("Null result!");
		} else {
			resultCount++;
			for(ActionResult actionResult: testResult.getActionResults()) {
				Integer statusCode = actionResult.getStatusCode();
				if (resultsByStatusCode.containsKey(statusCode)) {
					resultsByStatusCode.put(actionResult.getStatusCode(), resultsByStatusCode.get(statusCode).intValue() + 1);
				} else {
					resultsByStatusCode.put(actionResult.getStatusCode(), 1);
				}
				actionCount++;
				requestCount += actionResult.getRequestCount();
				totalRequestDuration += actionResult.getRequestDurationMs();
				durationByAction.add(actionResult);
				//System.out.println(actionResult.getDurationMs());
				totalActionDuration += actionResult.getRequestDurationMs();
			}
			if(testResult.isPassed()) {
				passedCount++;
			}
			else {
				failedCount++;
			}
		}
	}

	public String toHtml() {
		StringBuilder ret = new StringBuilder();
		ret.append("<pre>");
		ret.append(toString());
		ret.append("</pre>");
		ret.append("Error Pages: <br />");
		for (String fn : errorPages) {
			ret.append("<a href=\"").append(fn).append("\">").append(fn).append("</a><br/>").append(CRLF);
		}
		ret.append("</body></html>");
		return ret.toString();
	}
	
	public String toString() {
		double duration = endTick-startTick;
		StringBuilder ret = new StringBuilder();
		ret.append("Test Result: ").append(cx.getName()).append(CRLF);
		ret.append("Test started: ").append((new Date(startTick)).toString()).append(CRLF);
		ret.append("Test duration: ").append(NumberFormat.getInstance().format(duration)).append("ms").append(CRLF);
		double testsPerSec = (double)resultCount/(duration/1000.0);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		String testsPerSecString = nf.format(testsPerSec);
		double actionsPerSec = (double)actionCount/(duration/1000.0);
		String actionPerSecString = nf.format(actionsPerSec);
		double requestsPerSec = (double)requestCount/(duration/1000.0);
		String requestPerSecString = nf.format(requestsPerSec);
		ret.append("Tests Performed: ").append(resultCount).append(" (").append(testsPerSecString).append(" tests/s)").append(CRLF);
		ret.append("Actions Performed: ").append(actionCount).append(" (").append(actionPerSecString).append(" actions/s)").append(CRLF);
		ret.append("Avg Action Duration: ").append(actionCount==0 ? 0 : totalActionDuration / actionCount).append("ms").append(CRLF);
		ret.append("Requests Made: ").append(requestCount).append(" (").append(requestPerSecString).append(" requests/s)").append(CRLF);
		ret.append("Avg Request Duration: ").append(requestCount == 0 ? 0 : totalRequestDuration / requestCount).append("ms").append(CRLF);
		ret.append("Avg Duration by Action:").append(CRLF);
		NumberFormat nf2 = NumberFormat.getIntegerInstance();
		for(Map.Entry<String, Average> entry : durationByAction.getResults().entrySet()) {
			ret.append("  ").append(entry.getKey()).append(": ").append(nf2.format(entry.getValue().getAverage())).append("ms (x").append(entry.getValue().getCount()).append(')').append(CRLF);
		}
		ret.append("Passed: ").append(passedCount).append("; Failed: ").append(failedCount).append(CRLF);
		ret.append("Tests with Null Results: ").append(nullResultCount).append(CRLF);
		ret.append("Status Codes:\r\n");
		for (Entry<Integer, Integer> entry : resultsByStatusCode.entrySet()) {
			ret.append(entry.getKey()).append(":").append(entry.getValue()).append(CRLF);
		}
		return ret.toString();
	}

	public void testStarting() {
		log.info("Test Starting!");
		startTick = System.currentTimeMillis();
	}

	public void testEnded() {
		log.info("Test Complete.");
		endTick = System.currentTimeMillis();
	}

	public long size() {
		return requestCount;
	}
	
}
