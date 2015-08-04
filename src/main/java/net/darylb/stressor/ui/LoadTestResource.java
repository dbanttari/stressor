package net.darylb.stressor.ui;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.darylb.stressor.FixedLoadTest;
import net.darylb.stressor.InMemoryFileLogger;
import net.darylb.stressor.Interval;
import net.darylb.stressor.LoadTest;
import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.LoadTestDefinition;
import net.darylb.stressor.LoadTestResults;
import net.darylb.stressor.RateLimiter;
import net.darylb.stressor.RateLimiterImpl;
import net.darylb.stressor.TimedLoadTest;
import net.darylb.stressor.switchboard.Error404;
import net.darylb.stressor.switchboard.JsonRequestHandler;
import net.darylb.stressor.switchboard.Method;
import net.darylb.stressor.switchboard.RequestHandler;
import net.darylb.stressor.switchboard.RequestHandlerException;
import net.darylb.stressor.switchboard.RequestHandlerLocator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTestResource extends JsonRequestHandler implements RequestHandlerLocator {
	
	private static Logger log = LoggerFactory.getLogger(LoadTestResource.class);
	
	static LinkedHashMap<String, LoadTest> activeLoadTests = new LinkedHashMap<String, LoadTest>();
	
	public LoadTestResource() {
		super(JSONP_TOKEN);
	}
	
	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		URI = URI.toLowerCase();
		if(URI.startsWith("/loadtest/") || URI.equals("/loadtest")) {
			return this;
		}
		return null;
	}

	@Override
	public String get(String URI, HttpServletRequest req) throws java.io.IOException {
		if (URI.equalsIgnoreCase("/loadtest")) {
			return getActiveList();
		}
		else {
			String[] args = URI.split("/");
			String name = args[args.length-2];
			String timestamp = args[args.length-1];
			return getStatus(name, timestamp);
		}
	}
	
	@Override
	public String post(String URI, HttpServletRequest req, String content) throws java.io.IOException, RequestHandlerException {
		if (URI.equalsIgnoreCase("/loadtest")) {
			throw new RequestHandlerException(405, "Method Not Allowed");
		}
		else {
			String[] args = URI.split("/");
			String name = args[args.length-1];
			return create(name, content);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getActiveList() {
		JSONArray ret = new JSONArray();
		for(Entry<String, LoadTest> test : activeLoadTests.entrySet()) {
			JSONObject o = new JSONObject();
			o.put(test.getKey(), test.getValue());
			ret.add(o);
		}
		return ret.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public String create(String name, String json) {
		log.debug(json);
		LoadTestParams params = LoadTestParams.fromJson(json);
		
		final LoadTest loadTest = createLoadTest(params);
		String testName = name + "/" + loadTest.getStartTime();
		activeLoadTests.put(testName, loadTest);
		Thread t = new Thread() {
			public void run() {
				loadTest.run();
			}
		};
		t.setName("Load Test " + testName);
		t.setDaemon(true);
		t.start();
		
		log.debug(params.definition);
		JSONObject ret = new JSONObject();
		ret.put("name", name);
		ret.put("started", loadTest.getStartTime());
		return ret.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public String getStatus(String name, String timestamp) throws IOException {
		LoadTest loadTest = activeLoadTests.get(name+"/"+timestamp);
		if(loadTest == null) {
			throw new Error404();
		}
		else {
			JSONObject status = new JSONObject();
			status.put("completionPct", loadTest.getProgressPct());
			status.put("status", loadTest.getStatus().toString());
			LoadTestResults results = loadTest.getTestResults();
			if(results != null) {
				status.put("results", results.toJson());
			}
			return status.toJSONString();
		}
	}

	private LoadTest createLoadTest(LoadTestParams params) {
		LoadTestDefinition def;
		try {
			def = (LoadTestDefinition) Class.forName(params.definition).newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.error("Unable to instantiate load test class", e);
			throw new RuntimeException(e);
		}
		LoadTestContext cx = def.getLoadTestContext();
		cx.setFileLogger(new InMemoryFileLogger());
		if(params.limitInterval != null) {
			RateLimiter rateLimiter = new RateLimiterImpl(params.limitCount, Interval.getInterval(params.limitInterval));
			cx.setRateLimiter(rateLimiter);
		}
		LoadTest ret;
		if(params.type.equals("counted")) {
			// fixed iteration count
			ret = new FixedLoadTest(cx, def.getStoryFactory(cx), params.threads, params.iterationCount);
		}
		else {
			// timed
			ret = new TimedLoadTest(cx, def.getStoryFactory(cx), params.threads, params.durationMs);
		}
		return ret;
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}
	
}
