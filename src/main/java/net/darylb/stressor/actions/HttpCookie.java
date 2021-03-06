package net.darylb.stressor.actions;

public class HttpCookie {

	String name;
	String value;
	String domain;
	String expires;
	String path;
	
	public HttpCookie(String rawCookie) {
		String[] parts = rawCookie.split(";");
		boolean isFirst = true;
		for(String part : parts) {
			String[] nv = part.split("=");
			String name = nv[0];
			String value = nv.length < 2 ? "" : nv[1];
			if(isFirst) {
				// the actual cookie name/value
				this.name = name;
				this.value = value;
				isFirst = false;
			}
			else if(name.equalsIgnoreCase("domain")) {
				this.domain = value;
			}
			else if(name.equalsIgnoreCase("expires")) {
				this.expires = value;
			}
			else if(name.equalsIgnoreCase("path")) {
				this.path = value;
			}
		}
	}
	
	@Override
	public String toString() {
		return "Cookie '" + name + "': '" + value + "'";
	}
	
	
}
