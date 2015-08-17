package net.darylb.stressor.actions;

import java.util.concurrent.TimeoutException;

import net.darylb.stressor.LoadTestContext;

public class PendingRequestWaitAction extends Action {

	private final long timeoutMs;

	public PendingRequestWaitAction(final long timeoutMs) {
		this.timeoutMs = timeoutMs;
		
	}
	
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
		try {
			semaphore.join(timeoutMs);
		}
		catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		ret.setStatus(0);
		return ret;
	}



}
