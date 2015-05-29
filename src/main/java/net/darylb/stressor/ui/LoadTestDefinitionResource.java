package net.darylb.stressor.ui;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.darylb.stressor.LoadTestDefinition;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.reflections.Reflections;

@Path("/loadtestdefinition")
public class LoadTestDefinitionResource {
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String get() {
		Set<Class<? extends LoadTestDefinition>> implementations = getImplementations();
		JSONArray ret = new JSONArray();
		for(Class<? extends LoadTestDefinition> test : implementations) {
			JSONObject clazz = new JSONObject();
			clazz.put("canonicalName", test.getCanonicalName());
			clazz.put("name", test.getSimpleName());
			ret.add(clazz);
		}
		return ")]}',\n" + ret.toJSONString();
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
