package net.darylb.stressor.switchboard;

import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.actions.PendingRequestSemaphore;

/**
 * Use this if you're using tokens to create one-off requests using content
 * tokens.
 * 
 * @author daryl
 *
 */
public class PendingRequestHandlerLocator implements RequestHandlerLocator {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(PendingRequestHandlerLocator.class);
	
	// weak hash map won't hold the reference to the key, so if
	// PendingRequestSemaphore can be collected because the story has completed
	// (presumably unsuccessfully), this won't prevent that colection.
	WeakHashMap<String, PendingRequestSemaphore> waiting = new WeakHashMap<String, PendingRequestSemaphore>();

	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		String token = getToken(method, URI, req);
		synchronized (waiting) {
			return waiting.remove(token);
		}
	}

	public void register(String token, PendingRequestSemaphore semaphore) {
		synchronized (waiting) {
			waiting.put(token, semaphore);
		}
	}

	/**
	 * The default implementation extracts the last item of the URI as the token
	 * (delimited by / or =) If you want to override,
	 * 
	 * @param method
	 * @param URI
	 * @param req
	 * @return
	 */
	public static String getToken(Method method, String URI, HttpServletRequest req) {
		String[] tokens = URI.split("[/=]");
		return tokens.length==0 ? null : tokens[tokens.length - 1];
	}

}
