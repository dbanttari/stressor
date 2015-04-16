package net.darylb.stressor;

public abstract class Action {

	protected TestContext cx;

	public Action(TestContext cx) {
		this.cx = cx;
	}
	
	public abstract ActionResult call();
	
	/**
	 * Returns failure reason, or null for pass
	 * @param content
	 * @return
	 */
	protected String validate(String content) throws Exception {
		return null;
	}
	
	int n = 0;
	protected void logAction(String result) {
		Util.writeFile(cx.getLogDir(), this.getClass().getSimpleName()  + "." + Integer.toString(n++) + ".txt", result);
	}
	
}
