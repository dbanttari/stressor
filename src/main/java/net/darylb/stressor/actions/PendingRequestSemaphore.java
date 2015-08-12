package net.darylb.stressor.actions;

public class PendingRequestSemaphore {

	/**
	 * to prevent possible race conditions between RegisterAction and WaitAction,
	 * we use this flag to short-circuit join() into immediately responding.
	 */
	private boolean triggered = false;
	
	public void trigger() {
		synchronized (this) {
			triggered = true;
		}
	}
	
	public void join(long timeoutMs) {
		synchronized (this) {
			if(!triggered) {
				try {
					this.wait(timeoutMs);
				}
				catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
		}
	}
	
}
