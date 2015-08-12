package net.darylb.stressor.actions;

import net.darylb.stressor.LoadTestContext;

public class PendingRequestWaitAction extends Action {

	@Override
	public ActionResult call(LoadTestContext cx) {
		// I considered creating the ActionResult in the "register" action, but that may
		// lead to the sum of the Action times being greater than the total Story time,
		// which would cause a Star Trek style temporal anomaly that might destroy reality
		// as we know it.
		// 
		// Going the safe route.
		ActionResult ret = new ActionResult(this.getClass().getName());
		PendingRequestSemaphore semaphore = (PendingRequestSemaphore)cx.getStoryObject(Props.STRESSOR_PENDING_CALLBACK_STORY_SEMAPHORE);
		synchronized(semaphore) {
			semaphore.trigger();
		}
		ret.setStatus(0);
		return ret;
	}



}
