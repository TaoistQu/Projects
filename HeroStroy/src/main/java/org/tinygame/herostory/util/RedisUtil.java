package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis实用工具类
 *
 * @author QuLei
 */
public final class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
    static private JedisPool _jedisPool = null;


    /**
     * 私有化类默认构造方法
     */
    private RedisUtil() {
    }

    /**
     * 初始化redis
     */
    static public void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取redis对象
     *
     * @return redis对象
     */
    static public Jedis getRedis() {
        if (null == _jedisPool) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }
        Jedis redis = _jedisPool.getResource();
        return redis;
    }
}
