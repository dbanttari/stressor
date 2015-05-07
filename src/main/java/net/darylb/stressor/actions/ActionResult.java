package net.darylb.stressor.actions;

public class ActionResult {

	private int statusCode;
	private String content;
	private Throwable exception;
	private boolean passed = true;
	private String reason;
	private int requestCount;
	private long requestDurationMs;
	private String name;
	private long startTick;

	public ActionResult(String name) {
		this.name = name;
		this.startTick = System.currentTimeMillis();
	}
	
	public void setStatus(int statusCode) {
		setDuration();
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		setDuration();
		this.content = content;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		setDuration();
		this.exception = exception;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setFail(String reason) {
		setDuration();
		this.setReason(reason);
		this.passed = false;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		setDuration();
		this.reason = reason;
	}

	public void setValid(String reason) {
		setDuration();
		if(reason == null) {
			passed = true;
		}
		else {
			System.out.println("Test failed: " + reason);
			setFail(reason);
		}
	}

	private void setDuration() {
		this.requestDurationMs = System.currentTimeMillis() - startTick;
	}

	public void setRequestCount(int hitCount) {
		setDuration();
		this.requestCount = hitCount;
	}
	
	public int getRequestCount() {
		return this.requestCount;
	}

	public long getRequestDurationMs() {
		return requestDurationMs;
	}
	
	public long getAverageRequestDurationMs() {
		return requestCount==0 ? 0 : requestDurationMs / (long)requestCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}