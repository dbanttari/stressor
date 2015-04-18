package net.darylb.stressor;

public abstract class Action {

	public abstract ActionResult call(TestContext cx);
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	protected String validate(TestContext cx, String content) throws Exception {
		return null;
	}
	
}
