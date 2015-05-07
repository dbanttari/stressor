package net.darylb.stressor.actions;

import net.darylb.stressor.TestContext;
import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

public class MockAction extends Action {

	@Override
	public ActionResult call(TestContext cx) {
		return null;
	}

}
