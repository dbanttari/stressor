package net.darylb.stressor;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.darylb.stressor.actions.ActionResult;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTestResults {

	private Logger log = LoggerFactory.getLogger(LoadTestResults.class);
	
	private HashMap<Integer, Integer> actionResultsByStatusCode = new HashMap<Integer, Integer>();
	private AverageByLabel durationByAction = new AverageByLabel();
	private long endTick;
	private long startTick;
	private long storyCount = 0;
	private long nullResultCount = 0;
	private long passedCount = 0;
	private long failedCount = 0;
	private long actionCount = 0;
	private long totalStoryDurationMs = 0;
	private long requestCount = 0;
	private long totalActionDurationMs = 0;
	private LinkedList<String> errorPages = new LinkedList<String>();
	private final String CRLF = System.getProperty("line.separator");

	private LoadTestContext cx;

	public LoadTestResults(LoadTestContext cx) {
		this.cx = cx;
	}

	synchronized public void addResult(StoryResult storyResult) {
		if (storyResult == null) {
			nullResultCount++;
			System.out.println("Null result!");
		} else {
			storyCount++;
			for(ActionResult actionResult: storyResult.getActionResults()) {
				Integer statusCode = actionResult.getStatusCode();
				if (actionResultsByStatusCode.containsKey(statusCode)) {
					actionResultsByStatusCode.put(actionResult.getStatusCode(), actionResultsByStatusCode.get(statusCode).intValue() + 1);
				} else {
					actionResultsByStatusCode.put(actionResult.getStatusCode(), 1);
				}
				actionCount++;
				requestCount += actionResult.getRequestCount();
				totalActionDurationMs += actionResult.getRequestDurationMs();
				durationByAction.add(actionResult);
				//System.out.println(actionResult.getDurationMs());
				totalStoryDurationMs += actionResult.getRequestDurationMs();
			}
			if(storyResult.isPassed()) {
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
		NumberFormat nf0 = NumberFormat.getIntegerInstance();
		NumberFormat nf1 = NumberFormat.getInstance();
		nf1.setMaximumFractionDigits(1);

		long testDurationMs = (endTick==0 ? System.currentTimeMillis() : endTick) - startTick;
		double storyDurationSec = totalStoryDurationMs / 1000;
		double storiesPerSec = storyDurationSec==0.0 ? 0.0 : (double)storyCount / storyDurationSec;
		double storiesAvgDurMs = storyCount==0 ? 0.0 : (double)totalStoryDurationMs / (double)storyCount;
		double actionDurationSec = totalActionDurationMs / 1000;
		double actionsPerSec = (double)actionCount / actionDurationSec;
		double actionsAvgDurMs = actionCount==0 ? 0.0 : (double)totalStoryDurationMs / (double)actionCount;
		
		StringBuilder ret = new StringBuilder();
		ret.append("Test Result: ").append(cx.getName()).append(CRLF);
		ret.append("Test started: ").append((new Date(startTick)).toString()).append(CRLF);
		ret.append("Test duration: ").append(nf0.format(testDurationMs)).append("ms").append(CRLF);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		ret.append("Stories Performed: ").append(storyCount).append(" (").append(nf1.format(storiesPerSec)).append(" stories/s)").append(CRLF);
		ret.append("Actions Performed: ").append(actionCount).append(" (").append(nf1.format(actionsPerSec)).append(" actions/s)").append(CRLF);
		ret.append("Avg Story Duration: ").append(nf1.format(storiesAvgDurMs)).append("ms").append(CRLF);
		ret.append("Avg Action Duration: ").append(nf1.format(actionsAvgDurMs)).append("ms").append(CRLF);
		ret.append("Avg Duration by Action:").append(CRLF);
		for(Map.Entry<String, Average> entry : durationByAction.getResults().entrySet()) {
			ret.append("  ").append(entry.getKey()).append(": ").append(nf0.format(entry.getValue().getAverage())).append("ms (x").append(entry.getValue().getCount()).append(')').append(CRLF);
		}
		ret.append("Passed: ").append(passedCount).append("; Failed: ").append(failedCount).append(CRLF);
		ret.append("Tests with Null Results: ").append(nullResultCount).append(CRLF);
		ret.append("Status Codes:\r\n");
		for (Entry<Integer, Integer> entry : actionResultsByStatusCode.entrySet()) {
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

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		NumberFormat nf0 = NumberFormat.getIntegerInstance();
		NumberFormat nf1 = NumberFormat.getInstance();
		nf1.setMaximumFractionDigits(1);

		long testDurationMs = (endTick==0 ? System.currentTimeMillis() : endTick) - startTick;
		double storyDurationSec = totalStoryDurationMs / 1000;
		double storiesPerSec = storyDurationSec==0.0 ? 0.0 : (double)storyCount / storyDurationSec;
		double storiesAvgDurMs = storyCount==0 ? 0.0 : (double)totalStoryDurationMs / (double)storyCount;
		double actionDurationSec = totalActionDurationMs / 1000;
		double actionsPerSec = (double)actionCount / actionDurationSec;
		double actionsAvgDurMs = actionCount==0 ? 0.0 : (double)totalStoryDurationMs / (double)actionCount;

		JSONObject ret = new JSONObject();
		ret.put("name", cx.getName());
		ret.put("startTick", startTick);
		ret.put("durationMs", testDurationMs);
		
		JSONObject stories = new JSONObject();
		stories.put("numCompleted", storyCount);
		stories.put("numCompletedPerSecond", nf1.format(storiesPerSec));
		stories.put("avgDurationMs", nf1.format(storiesAvgDurMs));
		stories.put("numPassed", passedCount);
		stories.put("numFailed", failedCount);
		stories.put("numNull", nullResultCount);
		ret.put("stories", stories);
		
		JSONObject actions = new JSONObject();
		ret.put("completed", actionCount);
		ret.put("numCompletedPerSecond", nf1.format(actionsPerSec));
		ret.put("avgDurationMs", nf1.format(actionsAvgDurMs));
		JSONObject actionDetails = new JSONObject();
		for(Map.Entry<String, Average> entry : durationByAction.getResults().entrySet()) {
			JSONObject actionInfo = new JSONObject();
			Average avg = entry.getValue();
			actionInfo.put("count", avg.getCount());
			actionInfo.put("durationMs", avg.getTotal());
			actionInfo.put("avgDurationMs", nf0.format(avg.getAverage()));
			
			actionDetails.put(entry.getKey(), actionInfo);
		}
		actions.put("detailByAction", actionDetails);
		JSONObject statusCodes = new JSONObject();
		for (Entry<Integer, Integer> entry : actionResultsByStatusCode.entrySet()) {
			statusCodes.put(entry.getKey(), entry.getValue());
		}
		actions.put("detailByStatusCode", statusCodes);
		ret.put("actions", actions);
		return ret;
	}
	
}
