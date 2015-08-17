package net.darylb.stressor.actions;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.switchboard.RequestHandlerLocator;
import net.darylb.stressor.switchboard.Switchboard;

/**
 * This should be used with LoadTestContext.registerCleanupAction() when using a
 * custom implementation of RequestHandlerLocator
 * @author daryl
 *
 */
public class SwitchboardCleanupLocatorAction extends Action {
	
	private RequestHandlerLocator locator;

	public SwitchboardCleanupLocatorAction(RequestHandlerLocator locator) {
		this.locator = locator;
	}
	
	@Override
	public ActionResult call(LoadTestContext cx) {
		Switchboard.getInstance().removeLocator(locator);
		return null;
	}

}
