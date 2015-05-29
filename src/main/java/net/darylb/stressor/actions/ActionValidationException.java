package net.darylb.stressor.actions;

public class ActionValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4099053566678488672L;

	public ActionValidationException(String reason) {
		super(reason);
	}

	public ActionValidationException(String reason, Throwable t) {
		super(reason, t);
	}

	public ActionValidationException(Throwable t) {
		super(t);
	}

}
