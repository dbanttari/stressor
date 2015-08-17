package net.darylb.stressor.actions;

import java.util.concurrent.TimeoutException;

import net.darylb.stressor.switchboard.RequestHandler;

/**
 * This extends RequestHandler because Switchboard will ping this as if it were one.
 * see PendingRequestSemaphoreImpl
 * @author daryl
 *
 */
public interface PendingRequestSemaphore extends RequestHandler {

	/**
	 * Wait for notification for up to this duration.
	 * @param timeoutMs maximum time to wait for notification
	 * @throws TimeoutException if no notification arrives before timeout
	 */
	void join(long timeoutMs) throws TimeoutException;

}
