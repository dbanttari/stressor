package net.darylb.stressor.switchboard;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Use this if you're using tokens to create one-off requests using content tokens.
 * 
 * @author daryl
 *
 */
public abstract class PendingRequestHandlerLocator implements RequestHandlerLocator {

	HashMap<String, Object> waiting = new HashMap<String, Object>();
	
	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		String token = getToken(method, URI, req);
		if(waiting.containsKey(token)) {
			Object o;
			synchronized (waiting) {
				o = waiting.remove(token);
			}
			synchronized (o) {
				o.notify();
			}
		}
		return null;
	}

	public void register(String token, Object waiting) {
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
