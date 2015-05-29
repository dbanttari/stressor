package net.darylb.stressor.ui;

import net.darylb.stressor.RateLimiterImpl;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LoadTestParams {
	
	public String definition;
	public String type;
	public int threads;
	public int limitCount;
	public String limitInterval;
	public int iterationCount;
	public long durationMs;

	public static LoadTestParams fromJson(String obj) {
		return fromJson((JSONObject)JSONValue.parse(obj));
	}
	
	// {"definition":"net.darylb.stressor.MockTestDefinition","type":"timed","threads":1,"dur":{"min":1},"limit":{"count":1,"interval":"s"}}
	public static LoadTestParams fromJson(JSONObject obj) {
		LoadTestParams ret = new LoadTestParams();
		ret.definition = obj.get("definition").toString();
		ret.type = obj.get("type").toString();
		ret.threads = Integer.parseInt(obj.get("threads").toString());
		if(ret.type.equals("counted")) {
			// iteration-counted load test
			ret.iterationCount = Integer.parseInt(obj.get("iterationCount").toString());
		}
		else {
			// timed load test
			JSONObject dur = (JSONObject)obj.get("dur");
			if(dur.containsKey("hr")) {
				ret.durationMs += Integer.parseInt(dur.get("hr").toString()) * RateLimiterImpl.Interval.HOUR.getIntervalMs();
			}
			if(dur.containsKey("min")) {
				ret.durationMs += Integer.parseInt(dur.get("min").toString()) * RateLimiterImpl.Interval.MINUTE.getIntervalMs();
			}
		}
		if(obj.containsKey("limit")) {
			JSONObject limit = (JSONObject)obj.get("limit");
			Object count = limit.get("count");
			Object interval = limit.get("interval");
			if(count != null && interval != null) {
				ret.limitCount = Integer.parseInt(count.toString());
				ret.limitInterval = interval.toString();
			}
		}
		return ret;
	}
	
}
