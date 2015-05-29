package net.darylb.stressor.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.LoadTestContext;
import redis.clients.jedis.Jedis;

public abstract class RedisAction extends Action {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(RedisAction.class);
	private static String jedisHost;
	
	/**
	 * Attempts to use JedisPool when using SSH port forwarding failed miserably.
	 * Instead, we're using a ThreadLocal Jedis to control the number of
	 * instances/connections created.
	 */
	static ThreadLocal<Jedis> jedisPool = new ThreadLocal<Jedis>() {
		protected Jedis initialValue() {
			return new Jedis(jedisHost);
		};
	};
	
	@Override
	 public ActionResult call(LoadTestContext cx) {
		RedisAction.jedisHost = cx.getProperty(Props.JEDIS_HOST);
		Jedis jedis = jedisPool.get();
		try {
			jedis.ping();
		}
		catch(Exception e) {
			jedis = new Jedis(jedisHost);
			jedis.ping();
			jedisPool.set(jedis);
		}
		ActionResult ret = null;
		try {
			ret = call(cx, jedis);
		}
		finally {
			jedis.close();
		}
		return ret;
	}

	public abstract ActionResult call(LoadTestContext cx, Jedis jedis);

}
