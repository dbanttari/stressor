package net.darylb.stressor;

import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTestHelper {
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoadTestHelper.class);
	
	public ActionResult runAction(LoadTestContext cx, Action action) {
		ActionResult ret = action.call(cx);
		if(ret != null) {
			try {
				action.validate(cx, ret.getContent());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
	
	public void sleep(long ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			// clear interrupted flag
			Thread.interrupted();
		}
	}
	
}
