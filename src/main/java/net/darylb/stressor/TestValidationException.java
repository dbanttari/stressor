package net.darylb.stressor;

public class TestValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4099053566678488672L;

	public TestValidationException(String reason) {
		super(reason);
	}

	public TestValidationException(String reason, Throwable t) {
		super(reason, t);
	}

	public TestValidationException(Throwable t) {
		super(t);
	}

}
