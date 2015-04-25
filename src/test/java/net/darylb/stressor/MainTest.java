package net.darylb.stressor;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

public class MainTest {

	@Test
	public void testGetLoadTest_Invalid() {
		TestDefinition testRunner = new MockTestDefinition();
		LinkedList<String> args = new LinkedList<String>();
		args.add("23");
		args.add("123z");
		LoadTest ret = Main.getTest(testRunner, args);
		assertNull(ret);
	}

	@Test
	public void testGetLoadTest_Fixed() {
		TestDefinition testRunner = new MockTestDefinition();
		LinkedList<String> args = new LinkedList<String>();
		args.add("23");
		args.add("123");
		LoadTest ret = Main.getTest(testRunner, args);
		assertEquals("FixedLoadTest", ret.getClass().getSimpleName());
	}
	
	@Test
	public void testGetLoadTest_Timed() {
		TestDefinition testRunner = new MockTestDefinition();
		LinkedList<String> args = new LinkedList<String>();
		args.add("23");
		args.add("123m");
		LoadTest ret = Main.getTest(testRunner, args);
		assertEquals("TimedLoadTest", ret.getClass().getSimpleName());
		assertEquals((System.currentTimeMillis() + 123L * 60L * 1000L) / 10L, ((TimedLoadTest)ret).getEndTick() / 10L);
	}
	
}
