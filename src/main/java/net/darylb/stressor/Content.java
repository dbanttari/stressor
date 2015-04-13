package net.darylb.stressor;

public class Content {

	private byte[] content;

	public Content(byte[] buf) {
		setContent(buf);
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	
}


