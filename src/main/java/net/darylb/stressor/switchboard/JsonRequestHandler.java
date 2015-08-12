package net.darylb.stressor.switchboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class JsonRequestHandler implements RequestHandler {

	public static String JSONP_TOKEN = ")]}',\n";

	private String responsePrefix;

	public JsonRequestHandler() {
		this("");
	}
	
	public JsonRequestHandler(String responsePrefix) {
		this.responsePrefix = responsePrefix;
	}
	
	@Override
	public void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if(method.equals(Method.GET)) {
			respond(resp, get(URI, req));
		}
		else if (method.equals(Method.POST)) {
			String json = getPostedContent(req);
			respond(resp, post(URI, req, json));
		}
		else if (method.equals(Method.HEAD)) {
			respond(resp, head(URI, req));
		}
		else if (method.equals(Method.PUT)) {
			String json = getPostedContent(req);
			respond(resp, put(URI, req, json));
		}
		else if (method.equals(Method.DELETE)) {
			respond(resp, delete(URI, req));
		}
	}

	// one (or more) of these should be overridden:
	public String get(String URI, HttpServletRequest req) throws IOException { throw new IOException("Unimplemented call"); }
	public String post(String URI, HttpServletRequest req, String json) throws IOException { throw new IOException("Unimplemented call"); }
	public String head(String URI, HttpServletRequest req) throws IOException { throw new IOException("Unimplemented call"); }
	public String put(String URI, HttpServletRequest req, String json) throws IOException { throw new IOException("Unimplemented call"); }
	public String delete(String URI, HttpServletRequest req) throws IOException { throw new IOException("Unimplemented call"); }
	
	public void respond(HttpServletResponse resp, String response) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.print(responsePrefix);
		out.print(response);
		out.close();
	}

	public String getPostedContent(HttpServletRequest req) throws IOException {
		ServletInputStream _in = req.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(_in));
		StringWriter _out = new StringWriter();
		PrintWriter out = new PrintWriter(_out);
		String line;
		while( (line = in.readLine()) != null) {
			out.println(line);
		}
		_in.close();
		return _out.toString();
	}

}
