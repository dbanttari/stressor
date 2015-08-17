package net.darylb.stressor.actions;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.darylb.stressor.LoadTestContext;
import net.darylb.stressor.MockLoadTestContext;
import net.darylb.stressor.switchboard.Method;
import net.darylb.stressor.switchboard.PendingRequestHandlerLocator;
import net.darylb.stressor.switchboard.RequestHandler;
import net.darylb.stressor.switchboard.Switchboard;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PendingRequestSemaphoreTest extends EasyMockSupport {

	@Mock
	private HttpServletRequest mockRequest;
	@Mock
	private HttpServletResponse mockResponse;
	@Mock
	private PendingRequestSemaphoreImpl mockSemaphore;
	@Mock(type=MockType.NICE)
	private ServletOutputStream mockServletOutputStream;
	@Mock
	private RequestHandler responseGenerator;
	
	@Test
	public void test() throws Exception {
		String token = PendingRequestRegisterAction.getNewRandomToken();
		String uri = "/foo/bar/" + token;
		// Inform EasyMock of our expected mock behavior
		expect(mockRequest.getRequestURI())
			.andReturn(uri)
			.atLeastOnce();
		expect(mockRequest.getContextPath())
			.andReturn("/");
		mockResponse.setStatus(200);
		mockResponse.setContentType("text/plain");
		expect(mockResponse.getOutputStream())
			.andReturn(mockServletOutputStream);
		responseGenerator.handle(Method.GET, uri, mockRequest, mockResponse);
		replayAll();

		// create our handler.  Normally handled in LoadTestDefinition.getPendingRequestHandlerLocator()
		PendingRequestHandlerLocator loc = new PendingRequestHandlerLocator();
		// add handler to Switchboard.  Normally handled in LoadTest.run()
		Switchboard switchboard = Switchboard.getInstance();
		switchboard.addLocator(loc);
		// register with LoadTestContext so RegisterAction can find it. Also done in LoadTest.run()
		LoadTestContext cx = new MockLoadTestContext();
		cx.put(Props.PENDING_REQUESTS_LOCATOR, loc);

		// here we'll pretend to be a story that
		// - registers a callback token
		// - receives an HTTP request with that token
		// - doesn't even wait 1ms when PendingRequestWaitAction is called (since the semaphore should have been poked during switchboard.handle())
		new PendingRequestRegisterAction(token, responseGenerator).call(cx);
		// pretend request happened.  Should notify the STRESSOR_PENDING_CALLBACK_STORY_SEMAPHORE story object
		switchboard.handle(Method.GET, mockRequest, mockResponse);
		// now wait for that request.  Should be no wait
		ActionResult ret = new PendingRequestWaitAction(1L).call(cx);
		assertTrue(ret.isPassed());
		
		verify(mockRequest);
		verify(mockSemaphore);
		
		switchboard.removeLocator(loc);
		
	}

}
