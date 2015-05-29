package net.darylb.stressor.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

public class MockAction extends Action {

	private static Logger log = LoggerFactory.getLogger(MockAction.class);
	
	private static int count = 0;
	public static void resetCount() {
		count = 0;
	}
	public static int getCount() {
		return count;
	}
	
	@Override
	public ActionResult call(LoadTestContext cx) {
		log.info("MockAction performed on thread {}", Thread.currentThread().getName());
		count++;
		return new ActionResult("MockAction");
	}

}
