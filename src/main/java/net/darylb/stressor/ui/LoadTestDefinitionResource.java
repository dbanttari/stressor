package net.darylb.stressor.ui;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.darylb.stressor.LoadTestDefinition;
import net.darylb.stressor.switchboard.JsonRequestHandler;
import net.darylb.stressor.switchboard.Method;
import net.darylb.stressor.switchboard.RequestHandler;
import net.darylb.stressor.switchboard.RequestHandlerLocator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.reflections.Reflections;

public class LoadTestDefinitionResource extends JsonRequestHandler implements RequestHandlerLocator {

	public LoadTestDefinitionResource() {
		super(JSONP_TOKEN);
	}
	
	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		if(URI.equalsIgnoreCase("/loadtestdefinition")) {
			return this;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String get(HttpServletRequest req) throws java.io.IOException {
		Set<Class<? extends LoadTestDefinition>> implementations = getImplementations();
		JSONArray ret = new JSONArray();
		for(Class<? extends LoadTestDefinition> test : implementations) {
			JSONObject clazz = new JSONObject();
			clazz.put("canonicalName", test.getCanonicalName());
			clazz.put("name", test.getSimpleName());
			ret.add(clazz);
		}
		return ret.toJSONString();
	}
	
	// shouldn't really be possible for this to change at runtime
	static Set<Class<? extends LoadTestDefinition>> implementations = null;
	public synchronized static Set<Class<? extends LoadTestDefinition>> getImplementations() {
		if(implementations==null) {
			Reflections reflections = new Reflections("net.darylb.stressor");
			implementations = reflections.getSubTypesOf(LoadTestDefinition.class);
		}
		return implementations;
	}

}
