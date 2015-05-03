package net.darylb.stressor;

import redis.clients.jedis.Jedis;

public abstract class RedisAction extends Action {

	Jedis jedis;
	
	@Override
	public ActionResult call(TestContext cx) {
		if(jedis==null) {
			jedis = new Jedis(cx.getProperty("jedis.host"));
		}
		return call(cx, jedis);
	}

	public abstract ActionResult call(TestContext cx, Jedis jedis);

}
