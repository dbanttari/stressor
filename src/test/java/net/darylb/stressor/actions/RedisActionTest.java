package net.darylb.stressor.actions;

import static org.junit.Assert.*;
import net.darylb.stressor.MockTestContext;
import net.darylb.stressor.TestContext;

import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisActionTest {

	public class TestRedisAction extends RedisAction {

		static final String KEY = "RedisActionTest";
		static final String VALUE = "Hello, Cruel World";		
		
		@Override
		public ActionResult call(TestContext cx, Jedis jedis) {
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
		MockTestContext cx = new MockTestContext();
		// redis must be available at this location:
		cx.setProperty("jedis.host", "staging-utility-1.aws.invision.works");
		new TestRedisAction().call(cx);
	}

}
