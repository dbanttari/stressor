package net.darylb.stressor;

public abstract class Action {

	public abstract ActionResult call();
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	protected String validate(String content) {
		return null;
	}
	
}
