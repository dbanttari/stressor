package net.darylb.stressor.switchboard;

import java.util.LinkedList;

public class Registry extends LinkedList<RequestHandler> {

	private static final long serialVersionUID = 1L;

	public void register(RequestHandler handler) {
		this.add(handler);
	}
	
}
