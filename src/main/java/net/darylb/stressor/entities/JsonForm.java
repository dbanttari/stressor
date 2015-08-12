package net.darylb.stressor.entities;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;

/**
 * A simple subclass of org.json.simple.JSONObject that adds
 * a (org.apache.http.entity.StringEntity)toEntity()
 * helper method suitable for use with net.darylb.stressor.actions.HttpPostAction
 * @author daryl
 *
 */
public class JsonForm extends JSONObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1151151369605525458L;

	public StringEntity toEntity() {
		StringEntity ret = new StringEntity(this.toString(), ContentType.create("application/json"));
		ret.setContentEncoding("utf-8");
		return ret;
	}

}
