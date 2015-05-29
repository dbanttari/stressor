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
	
	public ActionResult setStatus(int statusCode) {
		setDuration();
		this.statusCode = statusCode;
		return this;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getContent() {
		return content;
	}

	public ActionResult setContent(String content) {
		setDuration();
		this.content = content;
		return this;
	}

	public Throwable getException() {
		return exception;
	}

	public ActionResult setException(Throwable exception) {
		setDuration();
		this.exception = exception;
		return this;
	}

	public boolean isPassed() {
		return passed;
	}

	public ActionResult setFail(String reason) {
		setDuration();
		this.setReason(reason);
		this.passed = false;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public ActionResult setReason(String reason) {
		setDuration();
		this.reason = reason;
		return this;
	}

	public ActionResult setValid(String reason) {
		setDuration();
		if(reason == null) {
			passed = true;
		}
		else {
			System.out.println("Test failed: " + reason);
			setFail(reason);
		}
		return this;
	}

	private ActionResult setDuration() {
		this.requestDurationMs = System.currentTimeMillis() - startTick;
		return this;
	}

	public ActionResult setRequestCount(int hitCount) {
		setDuration();
		this.requestCount = hitCount;
		return this;
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

	public ActionResult setName(String name) {
		this.name = name;
		return this;
	}
	
	
}
