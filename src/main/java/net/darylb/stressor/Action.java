package net.darylb.stressor;

public abstract class Action {

	public abstract ActionResult call(TestContext cx);
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	protected void validate(TestContext cx, String content) throws Exception {
	}
	
	protected void invalid(String reason) throws TestValidationException {
		throw new TestValidationException(reason);
	}

	protected void invalid(String reason, Throwable t) throws TestValidationException {
		throw new TestValidationException(reason, t);
	}

	protected void invalid(Throwable t) throws TestValidationException {
		throw new TestValidationException(t);
	}
	
}
