package net.darylb.stressor.actions;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.switchboard.PendingRequestHandlerLocator;
import net.darylb.stressor.switchboard.RequestHandler;

/**
 * This should be used with PendingRequestWaitAction for when Stressor is being used with Tomcat as a callback endpoint.
 * Include cx.setStoryProperty(Props.PENDING_REQUEST_TOKEN) as the last token in the URI for the default locator
 * (PendingRequestHandlerLocator) to work.
 * 
 * If you want different behavior for finding waiting threads, subclass (or re-implement) PendingRequestHandlerLocator
 * and register it via Switchboard.getInstance().addLocator()  Custom RequestHandlerLocator implementations should unregister
 * themselves at the end of the test using 
 * 
 * @author daryl
 *
 */
public class PendingRequestRegisterAction extends Action {

	private final String token;
	private final RequestHandler responseGenerator;

	public PendingRequestRegisterAction(RequestHandler responseGenerator) {
		this(getNewRandomToken(), responseGenerator);
	}

	public PendingRequestRegisterAction(String token, RequestHandler responseGenerator) {
		this.token = token;
		this.responseGenerator = responseGenerator;
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
		// put story's semaphore into the context so WaitAction can find it
		PendingRequestSemaphore semaphore = new PendingRequestSemaphoreImpl(responseGenerator);
		cx.setStoryObject(Props.STRESSOR_PENDING_CALLBACK_STORY_SEMAPHORE, semaphore);
		
		// put pointer from token->semaphore so Switchboard can find it via the test's (possibly custom) PendingRequestHandlerLocator
		PendingRequestHandlerLocator pendingRequestHandlerLocator = (PendingRequestHandlerLocator)cx.get(Props.PENDING_REQUESTS_LOCATOR);
		pendingRequestHandlerLocator.register(getToken(), semaphore);

		cx.setStoryProperty(Props.PENDING_REQUEST_TOKEN, getToken());
		return null;
	}

	public String getToken() {
		return token;
	}

}
