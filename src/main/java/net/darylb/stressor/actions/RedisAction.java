package net.darylb.stressor.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.darylb.stressor.TestContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public abstract class RedisAction extends Action {

	private static Logger log = LoggerFactory.getLogger(RedisAction.class);
	
	static JedisPool jedisPool;
	
	@Override
	public ActionResult call(TestContext cx) {
		Jedis jedis;
		if(jedisPool==null) {
			log.info("Connecting to Redis host {}", cx.getProperty("jedis.host"));
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxTotal(4+cx.getNumThreads()*2); // maximum active connections
			poolConfig.setMinIdle(1);
			poolConfig.setTestOnBorrow(true);
			jedisPool = new JedisPool(poolConfig, cx.getProperty("jedis.host"), 6379, 10);
			long startTick = System.currentTimeMillis();
			jedis = jedisPool.getResource();
			long dur = System.currentTimeMillis() - startTick;
			log.info("Initial connection took {}ms", dur);
		}
		else {
			jedis = jedisPool.getResource();
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

	public abstract ActionResult call(TestContext cx, Jedis jedis);

}
