package net.darylb.stressor.switchboard;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {

	/**
	 * Actually handle the request. See {@link net.darylb.stressor.switchboard.RequestHandlerImpl RequestHandlerImpl} for some helper methods, eg <code>respondJson(resp, json)</code>
	 * @param method see {@link net.darylb.stressor.switchboard.Method}
	 * @param URI the request path, beginning with '/', but with the servlet prefix (if any) removed.
	 * @param req the current HttpServletRequest.  Please do not modify until the handle() phase.
	 * @return true, if you're the handler for this URI
	 */
	void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) throws IOException;

}
