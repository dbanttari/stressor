package net.darylb.stressor.actions;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.darylb.stressor.switchboard.Method;
import net.darylb.stressor.switchboard.RequestHandler;

/**
 * This will wait for handle() to be called (presumably by Switchboard)
 * When this happens, <code>triggered</code> will be set to true, then <code>join()</code>
 * will either be notified (if already waiting) or will short-circuit (if the trigger
 * happened before <code>join()</code> was called)
 * @author daryl
 *
 */
public class PendingRequestSemaphoreImpl implements PendingRequestSemaphore {

	/**
	 * to prevent possible race conditions between RegisterAction and
	 * WaitAction, we use this flag to short-circuit join() into immediately
	 * responding.
	 */
	private boolean triggered = false;
	private RequestHandler requestHandler;

	public PendingRequestSemaphoreImpl(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;

	}

	@Override
	public void join(long timeoutMs) throws TimeoutException {
		synchronized (this) {
			if (!triggered) {
				try {
					this.wait(timeoutMs);
					if (!triggered) {
						throw new TimeoutException();
					}
				}
				catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
		}
	}

	@Override
	public void handle(Method method, String URI, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		synchronized (this) {
			triggered = true;
			this.notify();
		}
		requestHandler.handle(method, URI, req, resp);
	}


}
