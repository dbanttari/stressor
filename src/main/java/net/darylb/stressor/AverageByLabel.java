package net.darylb.stressor;

import java.util.LinkedHashMap;
import java.util.Map;

public class AverageByLabel {

	private LinkedHashMap<String, Average> results = new LinkedHashMap<String, Average>();
	
	public synchronized void add(ActionResult result) {
		Average avg = results.get(result.getName());
		if(avg==null) {
			avg = new Average();
			results.put(result.getName(), avg);
		}
		avg.add(result.getDurationMs());
	}

	public Map<String, Average> getResults() {
		return results;
	}

}
