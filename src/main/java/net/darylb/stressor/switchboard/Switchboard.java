package net.darylb.stressor.switchboard;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Switchboard {

	private static final Logger log = LoggerFactory.getLogger(Switchboard.class);
	private static final Switchboard instance = new Switchboard();
	
	private LinkedList<RequestHandlerLocator> locators = new LinkedList<RequestHandlerLocator>();

	private Switchboard() {
		addLocator(new StressorTestRequestHandler());
	}
	
	public static Switchboard getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(Method method, HttpServletRequest req, HttpServletResponse resp) {
		// prefix is, effectively, the name of the war file.
		// In practice, this should be ROOT.war, for which Tomcat makes the prefix "/",
		// but for testing within Eclipse this is probably "/stressor" or whatever your Eclipse project name is.
		String prefix = req.getContextPath();
		if(prefix == null) {
			prefix = "/";
		}
		String _URI = req.getRequestURI();
		String URI = _URI.substring(prefix.length()-1);
		log.debug("{} request for {}", method, URI);
		boolean found = false;
		LinkedList<RequestHandlerLocator> locators;
		// to avoid ConcurrentModification errors, we'll iterate over a shallow copy of the list:
		synchronized(this.locators) {
			locators = (LinkedList<RequestHandlerLocator>) this.locators.clone();
		}
		for(RequestHandlerLocator locator : locators) {
			RequestHandler handler = locator.handles(method, URI, req);
			if(handler != null) {
				log.debug("Request for {} handled by {}", URI, locator.getClass().getName());
				found = true;
				try {
					handler.handle(method, URI, req, resp);
				}
				catch(Throwable t) {
					log.error("Error handling {} {}", method, req.getRequestURL(), t);
					new RequestHandlerError(t).handle(method, URI, req, resp);
				}
			}
		}
		if(!found) {
			log.warn("No handler found for {} {}", method, req.getRequestURL());
			new RequestHandlerError(404, "Not Found").handle(method, URI, req, resp);
		}
	}
	
	// helper methods to safely modify locators
	public void addLocator(RequestHandlerLocator handler) {
		synchronized(this.locators) {
			this.locators.addFirst(handler);
		}
	}
	
	public void removeLocator(RequestHandlerLocator handler) {
		synchronized(this.locators) {
			this.locators.remove(handler);
		}
	}
	
}
