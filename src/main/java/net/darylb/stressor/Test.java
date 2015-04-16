package net.darylb.stressor;

import java.util.concurrent.Callable;

public abstract class Test implements Callable<TestResult> {

	private TestResult testResult;

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

	public abstract Action getNextAction(Action previousAction);

	@Override
	public TestResult call() {
		TestResult testResult = new TestResult(getTestName());
		Action previousAction = null, action;
		boolean lastActionPassed = true;
		while(lastActionPassed && (action = getNextAction(previousAction)) != null) {
			long startTick = System.currentTimeMillis();
			ActionResult actionResult = action.call();
			actionResult.setDurationMs(System.currentTimeMillis() - startTick);
			testResult.addActionResult(actionResult);
			lastActionPassed = actionResult.isPassed();
			previousAction = action;
		}
		onTestComplete();
		// free up content memory
		for(ActionResult actionResult : testResult.getActionResults()) {
			actionResult.setContent(null);
		}
		return testResult;
	}
	
	/**
	 * If you want to use any actionResult.content do so here.  (eg to save error pages)
	 */
	protected void onTestComplete() {
		// exists to be overridden
	}

	protected String getTestName() {
		return this.getClass().getSimpleName();
	}

}
