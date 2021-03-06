package net.darylb.stressor.actions;

import static org.junit.Assert.*;
import net.darylb.stressor.MockLoadTestContext;
import net.darylb.stressor.LoadTestContext;

import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisActionTest {

	public class TestRedisAction extends RedisAction {

		static final String KEY = "RedisActionTest";
		static final String VALUE = "Hello, Cruel World";		
		
		@Override
		public ActionResult call(LoadTestContext cx, Jedis jedis) {
			jedis.set(KEY, VALUE);
			String ret = jedis.get(KEY);
			assertEquals(VALUE, ret);
			jedis.del(KEY);
			ret = jedis.get(KEY);
			assertNull(ret);
			return null;
		}
		
	}
	
	@Test @Ignore
	public void test() {
		MockLoadTestContext cx = new MockLoadTestContext();
		// redis must be available at this location:
		cx.setProperty("jedis.host", "localhost");
		new TestRedisAction().call(cx);
	}

}
