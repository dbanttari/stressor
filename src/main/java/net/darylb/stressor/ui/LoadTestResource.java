package net.darylb.stressor.ui;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

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
import net.darylb.stressor.Util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/loadtest")
public class LoadTestResource {
	
	private static Logger log = LoggerFactory.getLogger(LoadTestResource.class);
	
	static LinkedHashMap<String, LoadTest> activeLoadTests = new LinkedHashMap<String, LoadTest>();
	
	@Context UriInfo uriInfo;
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String get() {
		JSONArray ret = new JSONArray();
		for(Entry<String, LoadTest> test : activeLoadTests.entrySet()) {
			JSONObject o = new JSONObject();
			o.put(test.getKey(), test.getValue());
			ret.add(o);
		}
		return Util.JSONP_TOKEN + ret.toJSONString();
	}
	
	@POST
	@Path("/{name}")
	public Response create(@PathParam("name") String name, String json) {
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
		return Response.ok(loadTest.getStartTime(), "text/plain").build();
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{name}/{timestamp}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getStatus(@PathParam("name") String name, @PathParam("timestamp") String timestamp) {
		ResponseBuilder ret;
		LoadTest loadTest = activeLoadTests.get(name+"/"+timestamp);
		if(loadTest == null) {
			ret = Response.status(Response.Status.NOT_FOUND);
		}
		else {
			JSONObject status = new JSONObject();
			status.put("completionPct", loadTest.getProgressPct());
			status.put("status", loadTest.getStatus().toString());
			LoadTestResults results = loadTest.getTestResults();
			if(results != null) {
				status.put("results", results.toJson());
			}
			ret = Response.ok(status.toJSONString(), "application/json");
		}
		return ret.build();
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
	
}
