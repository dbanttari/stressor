package net.darylb.stressor.switchboard;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import net.darylb.stressor.actions.PendingRequestSemaphore;

/**
 * Use this if you're using tokens to create one-off requests using content tokens.
 * 
 * @author daryl
 *
 */
public class PendingRequestHandlerLocator implements RequestHandlerLocator {

	HashMap<String, PendingRequestSemaphore> waiting = new HashMap<String, PendingRequestSemaphore>();
	
	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		String token = getToken(method, URI, req);
		if(waiting.containsKey(token)) {
			PendingRequestSemaphore o;
			synchronized (waiting) {
				o = waiting.remove(token);
			}
			return o;
		}
		return null;
	}

	public void register(String token, PendingRequestSemaphore waiting) {
		synchronized(this.waiting) {
			this.waiting.put(token, waiting);
		}
	}
	
	/**
	 * The default implementation extracts the last item of the URI as the token (delimited by / or =)
	 * If you want to override, 
	 * @param method
	 * @param URI
	 * @param req
	 * @return
	 */
	public static String getToken(Method method, String URI, HttpServletRequest req) {
		String[] tokens = URI.split("[/=]");
		return tokens[tokens.length-1];
	}

}
