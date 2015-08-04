package net.darylb.stressor.switchboard;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Switchboard {

	private static final Logger log = LoggerFactory.getLogger(Switchboard.class);
	private static final Switchboard instance = new Switchboard();
	
	private HashMap<Method, LinkedList<RequestHandlerLocator>> handlers = new HashMap<Method, LinkedList<RequestHandlerLocator>>();

	private Switchboard() {
		for(Method m : Method.values()) {
			handlers.put(m, new LinkedList<RequestHandlerLocator>());
		}
		addLocator(Method.GET, new StressorTestRequestHandler());
	}
	
	public static Switchboard getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(Method method, HttpServletRequest req, HttpServletResponse resp) {
		// prefix is, effectively, the name of the war file.
		// In practice, this should be ROOT.war, for which Tomcat makes the prefix "/",
		// but for testing this is "stressor" or whatever your Eclipse project name is.
		String prefix = req.getContextPath();
		String URI = req.getRequestURI().substring(prefix.length());
		log.info("{} request for {}", method, URI);
		boolean found = false;
		LinkedList<RequestHandlerLocator> handlerList = handlers.get(method);
		LinkedList<RequestHandlerLocator> locators;
		// to avoid ConcurrentModification errors, we'll iterate over a shallow copy of the list:
		synchronized(handlerList) {
			locators = (LinkedList<RequestHandlerLocator>) handlerList.clone();
		}
		for(RequestHandlerLocator locator : locators) {
			RequestHandler handler = locator.handles(method, URI, req);
			if(handler != null) {
				log.info("Request for {} handled by {}", URI, locator.getClass().getName());
				found = true;
				try {
					handler.handle(method, URI, req, resp);
				}
				catch(RequestHandlerException e) {
					log.error("Error handling {} {}", method, req.getRequestURL(), e);
					new RequestHandlerError(e).handle(method, URI, req, resp);
				}
				catch(Throwable t) {
					log.error("Error handling {} {}", method, req.getRequestURL(), t);
					new RequestHandlerError(t).handle(method, URI, req, resp);
				}
				finally {
					if(!handler.isRepeatable()) {
						// make sure we're the only ones looking at _handlers when we remove this one
						synchronized(handlerList) {
							handlerList.remove(locator);
						}
					}
				}
			}
		}
		if(!found) {
			log.warn("No handler found for {} {}", method, req.getRequestURL());
			new RequestHandlerError(404, "Not Found").handle(method, URI, req, resp);
		}
	}
	
	// helper methods to safely modify locators
	public void addLocator(Method method, RequestHandlerLocator handler) {
		LinkedList<RequestHandlerLocator> handlerList = handlers.get(method);
		synchronized(handlerList) {
			handlerList.addFirst(handler);
		}
	}
	
	public void removeLocator(Method method, RequestHandlerLocator handler) {
		LinkedList<RequestHandlerLocator> handlerList = handlers.get(method);
		synchronized(handlerList) {
			handlerList.remove(handler);
		}
	}
	
}
