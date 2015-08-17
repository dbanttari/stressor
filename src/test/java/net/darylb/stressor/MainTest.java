package net.darylb.stressor;

import static org.junit.Assert.*;

import org.junit.Test;

public class MainTest {

	@Test
	public void testGetLoadTest_Invalid() throws Exception {
		LoadTestDefinition testRunner = new MockTestDefinition();
		String[] args = {"-threads","23"};
		LoadTest ret = Main.getTest(testRunner, Main.parseOptions(args));
		assertNull(ret);
	}

	@Test
	public void testGetLoadTest_Fixed() throws Exception {
		LoadTestDefinition testRunner = new MockTestDefinition();
		String[] args = {"-threads","23","-count","50"};
		LoadTest ret = Main.getTest(testRunner, Main.parseOptions(args));
		assertEquals("FixedLoadTest", ret.getClass().getSimpleName());
	}
	
	@Test
	public void testGetLoadTest_PropertyOverrides() throws Exception {
		LoadTestDefinition testRunner = new MockTestDefinition();
		String[] args = {"-threads", "23", "-count", "50", "-set", "foo=bar", "-set", "user.password=testing124"};
		Main.getTest(testRunner, Main.parseOptions(args));
		assertEquals("bar", testRunner.getLoadTestContext().getProperty("foo"));
		assertEquals("testing124", testRunner.getLoadTestContext().getProperty("user.password"));
	}
	
	@Test
	public void testGetLoadTest_Timed() throws Exception  {
		LoadTestDefinition testRunner = new MockTestDefinition();
		String[] args = {"-threads","23","-duration","123m"};
		LoadTest ret = Main.getTest(testRunner, Main.parseOptions(args));
		assertEquals("TimedLoadTest", ret.getClass().getSimpleName());
		assertEquals((System.currentTimeMillis() + 123L * 60L * 1000L) / 10L, ((TimedLoadTest)ret).getEndTick() / 10L);
	}
	
	@Test
	public void testGetLoadTest_Limited() throws Exception  {
		LoadTestDefinition testRunner = new MockTestDefinition();
		String[] args = {"-threads","23","-count","50","-limit","10s"};
		LoadTest ret = Main.getTest(testRunner, Main.parseOptions(args));
		assertEquals("FixedLoadTest", ret.getClass().getSimpleName());
		RateLimiterImpl rateLimiter = (RateLimiterImpl)testRunner.getLoadTestContext().getRateLimiter();
		assertEquals(100, rateLimiter.getRateLimit());
	}
	
	
}
