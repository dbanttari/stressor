package net.darylb.stressor.entites;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;

public class JsonForm extends JSONObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1151151369605525458L;

	public StringEntity toEntity() {
		return new StringEntity(this.toString(), ContentType.create("application/json"));
	}

}
