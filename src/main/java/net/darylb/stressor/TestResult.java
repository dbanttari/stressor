package net.darylb.stressor;

import java.util.LinkedList;
import java.util.List;

import net.darylb.stressor.actions.ActionResult;

public class TestResult {

	private static int nextResultNumber = 1;
	private int resultNumber = nextResultNumber++;
	
	private boolean isPassed = true;
	private List<ActionResult> actionResults = new LinkedList<ActionResult>();
	private Throwable exception;
	private final String name;

	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret
			.append("TestResult ")
			.append(resultNumber)
			.append(": ") 
			.append(name)
			.append(": ");
		if(isPassed) { 
			ret.append("Pass; ");
		}
		else {
			ret.append("Fail! ");
			for(ActionResult result : actionResults) {
				if(!result.isPassed()) {
					ret.append(result.getReason());
				}
			}
		}
		//ret.append(getNumRequests()).append(" requests");
		return ret.toString();
	}
	
	TestResult(String name) {
		this.name = name;
	}
	
	public boolean isPassed() {
		return isPassed;
	}

	public void setPassed(boolean isPass) {
		isPassed = isPass;
	}

	public int getNumRequests() {
		return actionResults.size();
	}

	public void addActionResult(ActionResult req) {
		if(!req.isPassed()) {
			setPassed(false);
		}
		actionResults.add(req);
	}

	public List<ActionResult> getActionResults() {
		return actionResults;
	}

	public void setException(Throwable t) {
		setPassed(false);
		this.exception = t;
	}
	
	public Throwable getException() {
		return exception;
	}

	public String getName() {
		return name;
	}
	
}
