package net.darylb.stressor.switchboard;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StressorTestRequestHandler implements RequestHandlerLocator, RequestHandler {

	@Override
	public RequestHandler handles(Method method, String URI, HttpServletRequest req) {
		if(method.equals(Method.GET) && URI.equalsIgnoreCase("/stressortest")) {
			return this;
		}
		return null;
	}

	@Override
	public void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		out.print("<html><head><title>Stressor Test Page</title></head><body><h2>Stressor!</h2>All looks happy!</body></html>");
		out.close();
	}

}
