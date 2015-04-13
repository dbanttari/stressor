package net.darylb.stressor;

public class ActionResult {

	private int statusCode;
	private String content;
	private Throwable exception;
	private boolean passed;
	private String reason;
	private long durationMs = -1;
	private int requestCount;
	private long requestDuration;
	private String name;

	public ActionResult(String name) {
		this.name = name;
	}
	
	public void setStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setFail(String reason) {
		this.setReason(reason);
		this.passed = false;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setValid(String reason) {
		if(reason == null) {
			passed = true;
		}
		else {
			System.out.println("Test failed: " + reason);
			setFail(reason);
		}
		
	}

	public void setDurationMs(long durationMs) {
		if(this.durationMs==-1) {
			this.durationMs = durationMs;
		}
	}
	
	public long getDurationMs() {
		if(this.durationMs==-1) {
			throw new IllegalStateException("Action duration not set by action!");
		}
		return this.durationMs;
	}

	public void setRequestCount(int hitCount) {
		this.requestCount = hitCount;
	}
	
	public int getRequestCount() {
		return this.requestCount;
	}

	public long getRequestDuration() {
		return requestDuration;
	}

	public void setRequestDuration(long requestDuration) {
		this.requestDuration = requestDuration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
