package net.darylb.stressor.switchboard;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandlerException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2822691677917930139L;

	public static final Logger log = LoggerFactory.getLogger(RequestHandlerException.class);
	
	private final int code;

	public RequestHandlerException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
