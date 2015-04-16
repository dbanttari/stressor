package net.darylb.stressor;

public class Cookie {

	String name;
	String value;
	String domain;
	String expires;
	String path;
	
	public Cookie(String rawCookie) {
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
