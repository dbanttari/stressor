package net.darylb.stressor.switchboard;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandlerError implements RequestHandler {

	public static final Logger log = LoggerFactory.getLogger(RequestHandlerError.class);
	
	private int code;

	private String message;

	public RequestHandlerError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public RequestHandlerError(Throwable t) {
		if(t instanceof RequestHandlerException) {
			this.code = ((RequestHandlerException)t).getCode();
		}
		else {
			this.code = 500;
		}
		this.message = t.getMessage();
	}

	@Override
	public void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp.sendError(code, message);
		}
		catch (IOException e) {
			// ignore
		}
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

}
