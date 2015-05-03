package net.darylb.stressor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;

public class SimpleJsonForm extends LinkedHashMap<String,String> {

	private static final long serialVersionUID = -9070367608287370432L;

	public StringEntity toEntity() {
		return new StringEntity(this.toString(), ContentType.create("application/json"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		for(Map.Entry<String, String>entry : this.entrySet()) {
			json.put(entry.getKey(), entry.getValue());
		}
		return json.toString();
	}
	
	@Override
	public String put(String key, String value) {
		if(value==null) {
			throw new IllegalArgumentException("Value for '" + key + "' may not be null");
		}
		return super.put(key, value);
	}
	
}
