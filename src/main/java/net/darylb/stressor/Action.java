package net.darylb.stressor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Action extends DatabaseHelper {

	private String name;
	
	public Action() {
		this.name = this.getClass().getSimpleName();
	}
	
	public abstract ActionResult call(TestContext cx);
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	protected void validate(TestContext cx, String content) throws Exception {
	}
	
	protected static void invalid(String reason) throws TestValidationException {
		throw new TestValidationException(reason);
	}

	protected static void invalid(String reason, Throwable t) throws TestValidationException {
		throw new TestValidationException(reason, t);
	}

	protected static void invalid(Throwable t) throws TestValidationException {
		throw new TestValidationException(t);
	}
	
	public String findString(Pattern p, String content) {
		return findString(p, content, "Pattern not found.");
	}
	public String findString(Pattern p, String content, String errorMessage) {
		if(content==null) {
			invalid("No content returned from action " + this.getClass().getSimpleName());
		}
		Matcher m = p.matcher(content);
		if(m.find()) {
			return m.group(1);
		}
		throw new RuntimeException(errorMessage);
	}
	
	public static String[] findStrings(Pattern p, String content) {
		return findStrings(p, content, "Pattern not found.");
	}
	public static String[] findStrings(Pattern p, String content, String errorMessage) {
		Matcher m = p.matcher(content);
		if(m.find()) {
			int count = m.groupCount();
			String[] ret = new String[count];
			for(int i=1; i <= count; i++) {
				ret[i] = m.group(i);
			}
			return ret;
		}
		throw new RuntimeException(errorMessage);
	}
	

	public static String findJson(String content, String elementName) {
		return findJson(content, elementName, "Value not found in JSON");
	}
	public static String findJson(String content, String elementName, String message) {
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject)parser.parse(content);
		}
		catch (ParseException e) {
			throw new RuntimeException(message, e);
		}
		Object value = json.get(elementName);
		if(value==null) {
			throw new RuntimeException(message);
		}
		return value.toString();
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
}
