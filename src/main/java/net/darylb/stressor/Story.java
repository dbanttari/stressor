package net.darylb.stressor;

import java.util.LinkedList;

import net.darylb.stressor.actions.Action;
import net.darylb.stressor.actions.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Story extends TestHelper {

	private static Logger log = LoggerFactory.getLogger(Story.class);
	
	private StoryResult testResult;

	private String name;
	
	private volatile static int errNumber = 0;

	public Story() {
		this.name = this.getClass().getSimpleName();
	}
	
	public StoryResult getTestResult() {
		return testResult;
	}

	public void setTestResult(StoryResult testResult) {
		this.testResult = testResult;
	}

	LinkedList<Action> actions;
	protected void addAction(Action action) {
		if(actions == null) {
			actions = new LinkedList<Action>();
		}
		actions.add(action);
	}
	
	protected Action getNextAction(TestContext cx, Action previousAction) {
		if(actions==null) {
			throw new RuntimeException("No actions were specified for this test!");
		}
		if(actions.isEmpty()) {
			return null;
		}
		return actions.remove();
	}

	public StoryResult call(TestContext cx) {
		cx.newStory();
		StoryResult testResult = new StoryResult(getName());
		Action previousAction = null, action;
		boolean lastActionPassed = true;
		while(lastActionPassed && (action = getNextAction(cx, previousAction)) != null) {
			try {
				ActionResult actionResult = action.call(cx);
				if(actionResult==null) {
					continue;
				}
				try {
					action.validate(cx, actionResult.getContent());
				}
				catch (Exception e) {
					String fn = "error-content-" + Integer.toString(errNumber++) + ".txt";
					log.error("Validation failed; content written to {}", fn, e);
					actionResult.setFail(e.toString());
					actionResult.setException(e);
					Util.writeFile(cx.getLogDir(), fn, actionResult.getContent());
				}
				testResult.addActionResult(actionResult);
				lastActionPassed = actionResult.isPassed();
				previousAction = action;
			}
			catch (Exception e) {
				log.error("Test failed", e);
				e.printStackTrace();
				ActionResult actionResult = new ActionResult(action.getName());
				actionResult.setFail(e.toString());
				actionResult.setException(e);
				testResult.addActionResult(actionResult);
				lastActionPassed = false;
				previousAction = action;
			}
		}
		onTestComplete(cx);
		// free up content memory
		for(ActionResult actionResult : testResult.getActionResults()) {
			actionResult.setContent(null);
		}
		return testResult;
	}
	
	/**
	 * If you want to use any actionResult.content do so here.  (eg to save error pages)
	 */
	protected void onTestComplete(TestContext cx) {
	}

	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

}
