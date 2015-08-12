package net.darylb.stressor.actions;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.switchboard.PendingRequestHandlerLocator;

public class PendingRequestRegisterAction extends Action {

	private final String token;

	public PendingRequestRegisterAction(String token) {
		this.token = token;
	}
	
	public PendingRequestRegisterAction() {
		this.token = getNewRandomToken();
	}
	
	/**
	 * Static method to get a /new/ random token, not to be confused with the instance getToken()
	 * @return An arbitrary random alphanumeric string
	 */
	public static String getNewRandomToken() {
	    return new BigInteger(128, ThreadLocalRandom.current()).toString(32);
	}

	@Override
	public ActionResult call(LoadTestContext cx) {
		PendingRequestHandlerLocator pending = (PendingRequestHandlerLocator)cx.get(Props.PENDING_REQUESTS_LOCATOR);
		PendingRequestSemaphore semaphore = new PendingRequestSemaphore();
		cx.setStoryObject(Props.STRESSOR_PENDING_CALLBACK_STORY_SEMAPHORE, semaphore);
		pending.register(getToken(), semaphore);
		return null;
	}

	public String getToken() {
		return token;
	}

}
