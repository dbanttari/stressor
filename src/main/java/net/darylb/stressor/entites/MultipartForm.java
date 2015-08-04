package net.darylb.stressor.entites;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class MultipartForm extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = -9070362348287370432L;

	public HttpEntity toEntity(String boundary) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for(Map.Entry<String, Object>entry : this.entrySet()) {
			if(entry.getValue() instanceof File) {
				File file = (File)entry.getValue();
				builder.addBinaryBody(entry.getKey(), file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
			}
			else {
				builder.addTextBody(entry.getKey(), entry.getValue().toString());
			}
		}
		builder.setBoundary(boundary);
		return builder.build();
	}
	
	@Override
	public String toString() {
		String amp = "";
		StringBuilder ret = new StringBuilder();
		for(Map.Entry<String, Object>entry : this.entrySet()) {
			ret.append(amp);
			amp = "&";
			if(entry.getValue() instanceof File) {
				ret.append(entry.getKey()).append('=').append( ((File)entry.getValue()).getAbsolutePath() );
			}
			else {
				ret.append(entry.getKey()).append('=').append( entry.getValue().toString() );
			}
		}
		return ret.toString();
	}
	
	@Override
	public Object put(String key, Object value) {
		if(value==null) {
			throw new IllegalArgumentException("Value for '" + key + "' may not be null");
		}
		return super.put(key, value);
	}
}
