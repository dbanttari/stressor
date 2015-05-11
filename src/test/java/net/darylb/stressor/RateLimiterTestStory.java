package net.darylb.stressor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

public class RateLimiterTestStory extends Story {

	private static Logger log = LoggerFactory.getLogger(RateLimiterTestStory.class);
	
	public RateLimiterTestStory() {
		addAction(new Action() {

			@Override
			public ActionResult call(TestContext cx) {
				log.debug("Action called!");
				return null;
			}
			
		});
	}
	
}
