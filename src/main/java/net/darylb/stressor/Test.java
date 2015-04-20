package net.darylb.stressor;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Test implements Callable<TestResult> {

	private static Logger log = LoggerFactory.getLogger(Test.class);
	
	private TestContext cx;

	protected Test(TestContext cx) {
		this.cx = cx;
	}
	
	private TestResult testResult;

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public abstract Action getNextAction(TestContext cx, Action previousAction);

	@Override
	public TestResult call() {
		cx.newTest();
		TestResult testResult = new TestResult(getTestName());
		Action previousAction = null, action;
		boolean lastActionPassed = true;
		while(lastActionPassed && (action = getNextAction(cx, previousAction)) != null) {
			try {
				ActionResult actionResult = action.call(cx);
				try {
					action.validate(cx, actionResult.getContent());
				}
				catch (Exception e) {
					log.error("Validation failed", e);
					actionResult.setFail(e.toString());
					actionResult.setException(e);
				}
				testResult.addActionResult(actionResult);
				lastActionPassed = actionResult.isPassed();
				previousAction = action;
			}
			catch (Exception e) {
				log.error("Test failed", e);
				e.printStackTrace();
				ActionResult actionResult = new ActionResult(action.getClass().getSimpleName());
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
		// exists to be overridden
	}

	protected String getTestName() {
		return this.getClass().getSimpleName();
	}

}
