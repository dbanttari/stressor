package net.darylb.stressor.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.LoadTestHelper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Action extends LoadTestHelper {

	private String name;
	
	public Action() {
		this.name = this.getClass().getSimpleName();
	}
	
	/**
	 * Implement to perform the action required by the story.
	 * @param cx
	 * @return an ActionResult object indicating whether the test succeeded
	 */
	public abstract ActionResult call(LoadTestContext cx);
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	public void validate(LoadTestContext cx, String content) throws Exception {
	}
	
	/**
	 * Helper method to throw a TestValidationException with the given reason string.
	 * @param reason The reason this test is invalid
	 * @throws ActionValidationException
	 */
	protected static void invalid(String reason) throws ActionValidationException {
		throw new ActionValidationException(reason);
	}

	/**
	 * Helper method to throw a TestValidationException with the given reason string and 'caused by' exception.
	 * @param reason The reason this test is invalid
	 * @param t the exception causing this validation failure
	 * @throws ActionValidationException
	 */
	protected static void invalid(String reason, Throwable t) throws ActionValidationException {
		throw new ActionValidationException(reason, t);
	}

	/**
	 * Helper method to throw a TestValidationException with the given 'caused by' exception.
	 * @param t the exception causing this validation failure
	 * @throws ActionValidationException
	 */
	protected static void invalid(Throwable t) throws ActionValidationException {
		throw new ActionValidationException(t);
	}
	
	/**
	 * Searches the content and returns the first capture group in the pattern supplied, or throws a RuntimeException if not found.
	 * @param content the content to apply the pattern to
	 * @param p the pattern to match.  Should include one capture group.
	 * @return the value of the first capture group matched.
	 */
	public String findString(String content, Pattern p) {
		return findString(content, p, "Pattern not found.");
	}
	/**
	 * Searches the content and returns the first capture group in the pattern supplied, or throws a RuntimeException if not found.
	 * @param content the content to apply the pattern to
	 * @param p the pattern to match.  Should include one capture group.
	 * @param errorMessage the message 
	 * @return the value of the first capture group matched.
	 */
	public String findString(String content, Pattern p, String errorMessage) {
		if(content==null) {
			invalid("No content returned from action " + this.getClass().getSimpleName());
		}
		Matcher m = p.matcher(content);
		if(m.find()) {
			return m.group(1);
		}
		throw new RuntimeException(errorMessage);
	}
	
	/**
	 * Will return every capture group in the content for the pattern supplied, or throw a RuntimeException if not found.
	 * @param p the pattern to match.  Should include one capture group.
	 * @param content the content to apply the pattern to
	 * @return the value of the first capture group matched.
	 */
	public static String[] findStrings(String content, Pattern p) {
		return findStrings(content, p, "Pattern not found.");
	}
	/**
	 * Will return every capture group in the content for the pattern supplied, or throw a RuntimeException if not found.
	 * @param p the pattern to match.  Should include one capture group.
	 * @param content the content to apply the pattern to
	 * @param errorMessage the error message used in the RuntimeException generated if the pattern isn't found
	 * @return the value of the first capture group matched.
	 */
	public static String[] findStrings(String content, Pattern p, String errorMessage) {
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
	

	/**
	 * Will attempt to find the value of the named element in the content, once parsed as JSON
	 * @param content the raw JSON-formatted string
	 * @param elementName the name of the top-level element being looked for
	 * @return the value of that element
	 */
	public static String findJson(String content, String elementName) {
		return findJson(content, elementName, "Value not found in JSON");
	}
	/**
	 * Will attempt to find the value of the named element in the content, once parsed as JSON
	 * @param content the raw JSON-formatted string
	 * @param elementName the name of the top-level element being looked for
	 * @param message the message to display in the RuntimeException generated if the element isn't found
	 * @return the value of that element
	 */
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

	public static void matchJson(String content, String elementName, String match) {
		matchJson(content, elementName, match, "Could not match JSON element");
	}
	/**
	 * Will attempt to find the value of the named element in the content, once parsed as JSON
	 * @param content the raw JSON-formatted string
	 * @param elementName the name of the top-level element being looked for
	 * @param message the message to display in the RuntimeException generated if the element isn't found
	 * @return the value of that element
	 */
	public static void matchJson(String content, String elementName, String match, String message) {
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject)parser.parse(content);
			Object value = json.get(elementName);
			if(value==null) {
				invalid(message);
			}
			if(!value.toString().equals(match)) {
				invalid(message);
			}
		}
		catch (Exception e) {
			invalid(message, e);
		}
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
}
