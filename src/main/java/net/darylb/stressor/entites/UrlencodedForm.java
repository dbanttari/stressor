package net.darylb.stressor.entites;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class UrlencodedForm extends LinkedHashMap<String,String> {

	private static final long serialVersionUID = -9070367608287370432L;

	public AbstractHttpEntity toEntity() {
		return new StringEntity(this.toString(), ContentType.create("application/x-www-form-urlencoded"));
	}
	
	@Override
	public String toString() {
		String amp = "";
		StringBuilder ret = new StringBuilder();
		for(Map.Entry<String, String>entry : this.entrySet()) {
			try {
				ret.append(amp).append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				// will never happen
			}
			amp = "&";
		}
		return ret.toString();
	}
	
	@Override
	public String put(String key, String value) {
		if(value==null) {
			throw new IllegalArgumentException("Value for '" + key + "' may not be null");
		}
		return super.put(key, value);
	}
	
}
