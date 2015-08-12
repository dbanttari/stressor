package net.darylb.stressor.switchboard;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class PendingRequestHandler implements RequestHandler {

	@Override
	public void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub

	}

}
