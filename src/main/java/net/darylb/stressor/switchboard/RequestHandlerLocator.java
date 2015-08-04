package net.darylb.stressor.switchboard;

import javax.servlet.http.HttpServletRequest;

public interface RequestHandlerLocator {

	/**
	 * Determines the request handler the handles this request. {@link #RequestLocator.handle(Method, String, HttpServletRequest, HttpServletResponse)} will then be called on the returned object.
	 * (This was done two-phase because if a RuntimeException is thrown by a combined handles/handler, it's impossible to know if a exception thrown by
	 * the 'handles' phase or by the 'handler' phase, so this way we can ignore 'handles' exceptions, instead of breaking the 'handles?' polling phase.)
	 * @param method @see Method
	 * @param URI the request path, beginning with '/', but with the servlet prefix (if any) removed.
	 * @param req the current HttpServletRequest.  Please do not modify until the handle() phase.
	 * @return the RequestHandler that handles this URI, or null if this class isn't (or can't locate) the handler.
	 */
	RequestHandler handles(Method method, String URI, HttpServletRequest req);

}
