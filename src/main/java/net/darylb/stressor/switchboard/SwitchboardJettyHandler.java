package net.darylb.stressor.switchboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SwitchboardJettyHandler extends AbstractHandler {

	private Switchboard switchboard;

	public SwitchboardJettyHandler(Switchboard switchboard) {
		this.switchboard = switchboard;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		switchboard.handle(Method.valueOf(req.getMethod()), req, resp);
		baseRequest.setHandled(true);
	}

}
