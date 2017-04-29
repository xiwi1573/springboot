package cn.org.xiwi.springboot.redis.utils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 工具类
 * 
 * @author caspar
 *
 */
public class RedisUtil {

	protected static ReentrantLock lockPool = new ReentrantLock();
	protected static ReentrantLock lockJedis = new ReentrantLock();

	protected static Logger logger = LoggerFactory.getLogger(RedisUtil.class.getSimpleName());

	// Redis服务器IP
	private static String ADDR_ARRAY = "192.168.2.108";//"10.10.152.26";

	// Redis的端口号
	private static int PORT = 6379;

	// 访问密码
	 private static String AUTH = "root123_";

	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 1024;;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 200;;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;;

	// 超时时间
	private static int TIMEOUT = 10000;;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;;

	private static JedisPool jedisPool = null;

	/**
	 * redis过期时间,以秒为单位
	 */
	public final static int EXRP_HOUR = 60 * 60; // 一小时
	public final static int EXRP_DAY = 60 * 60 * 24; // 一天
	public final static int EXRP_MONTH = 60 * 60 * 24 * 30; // 一个月

	/**
	 * 初始化Redis连接池
	 */
	private static void initialPool() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT,AUTH);
		} catch (Exception e) {
			logger.error("First create JedisPool error : " + e);
			try {
				// 如果第一个IP异常，则访问第二个IP
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(MAX_ACTIVE);
				config.setMaxIdle(MAX_IDLE);
				config.setMaxWaitMillis(MAX_WAIT);
				config.setTestOnBorrow(TEST_ON_BORROW);
				jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT,AUTH);
			} catch (Exception e2) {
				logger.error("Second create JedisPool error : " + e2);
			}
		}
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static void poolInit() {
		// 断言 ，当前锁是否已经锁住，如果锁住了，就啥也不干，没锁的话就执行下面步骤
		assert !lockPool.isHeldByCurrentThread();
		lockPool.lock();
		try {
			if (jedisPool == null) {
				initialPool();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lockPool.unlock();
		}
	}

	public static Jedis getJedis() {
		// 断言 ，当前锁是否已经锁住，如果锁住了，就啥也不干，没锁的话就执行下面步骤
		assert !lockJedis.isHeldByCurrentThread();
		lockJedis.lock();

		if (jedisPool == null) {
			poolInit();
		}
		Jedis jedis = null;
		try {
			if (jedisPool != null) {
				jedis = jedisPool.getResource();
			}
		} catch (Exception e) {
			logger.error("Get jedis error : " + e);
		} finally {
			returnResource(jedis);
			lockJedis.unlock();
		}
		return jedis;
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	@SuppressWarnings("deprecation")
	public static void returnResource(final Jedis jedis) {
		if (jedis != null && jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 设置 String
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized static void setString(String key, String value) {
		try {
			value = StringUtils.isEmpty(value) ? "" : value;
			getJedis().set(key, value);
		} catch (Exception e) {
			logger.error("Set key error : " + e);
		}
	}

	/**
	 * 设置 过期时间
	 * 
	 * @param key
	 * @param seconds
	 *            以秒为单位
	 * @param value
	 */
	public synchronized static void setString(String key, int seconds, String value) {
		try {
			value = StringUtils.isEmpty(value) ? "" : value;
			getJedis().setex(key, seconds, value);
		} catch (Exception e) {
			logger.error("Set keyex error : " + e);
		}
	}

	/**
	 * 获取String值
	 * 
	 * @param key
	 * @return value
	 */
	public synchronized static String getString(String key) {
		if (getJedis() == null || !getJedis().exists(key)) {
			return null;
		}
		return getJedis().get(key);
	}

	/**
	 * 设置对象
	 * 
	 * @param key
	 * @param obj
	 */
	public static void setObject(String key, Object obj) {
		try {
			obj = obj == null ? new Object() : obj;
			getJedis().set(key.getBytes(), SerializeUtil.serialize(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取对象
	 * 
	 * @param key
	 * @return Object
	 */
	public static Object getObject(String key) {
		if (getJedis() == null || !getJedis().exists(key)) {
			return null;
		}
		byte[] data = getJedis().get(key.getBytes());
		return (Object) SerializeUtil.unserialize(data);
	}

	/**
	 * 设置List集合
	 * 
	 * @param key
	 * @param list
	 */
	public static void setList(String key, List<?> list) {
		try {
			if (list != null && list.size() > 0) {
				getJedis().set(key.getBytes(), SerializeUtil.serializeList(list));
			} else {// 如果list为空,则设置一个空
				getJedis().set(key.getBytes(), "".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取List集合
	 * 
	 * @param key
	 * @return
	 */
	public static List<?> getList(String key) {
		if (getJedis() == null || !getJedis().exists(key)) {
			return null;
		}
		byte[] data = getJedis().get(key.getBytes());
		return SerializeUtil.unserializeList(data);
	}

	/**
	 * 删除key，可以是一个，也可以是多个key
	 * 
	 * @param keys
	 */
	public synchronized static void deleteKey(String... keys) {
		getJedis().del(keys);
	}

	/**
	 * 删除匹配的key<br>
	 * 如以my为前缀的则 参数为"my*"
	 * 
	 * @param key
	 */
	public synchronized static void deleteKeys(String pattern) {
		// 列出所有匹配的key
		Set<String> keySet = getJedis().keys(pattern);
		if (keySet == null || keySet.size() <= 0) {
			return;
		}
		String keyArr[] = new String[keySet.size()];
		int i = 0;
		for (String keys : keySet) {
			keyArr[i] = keys;
			i++;
		}
		deleteKey(keyArr);
	}

	/**
	 * 删除前缀为{参数}的所有key<br>
	 * 
	 * @param prefix
	 */
	public synchronized static void deleteKeyByPrefix(String prefix) {
		deleteKeys(prefix + "*");
	}

	/**
	 * 删除包含{参数}的所有key<br>
	 * 
	 * @param contain
	 */
	public synchronized static void deleteKeyByContain(String contain) {
		deleteKeys("*" + contain + "*");
	}

	/**
	 * 删除当前中所有key
	 */
	public synchronized static void flushdb() {
		getJedis().flushDB();
	}
}
