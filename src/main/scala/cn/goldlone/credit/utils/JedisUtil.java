package cn.goldlone.credit.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Created by CN on 2018/08/12/0012 12:21 .
 */
public class JedisUtil {

  private static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);

  private static String hostname;
  private static String port;
  private static String database;

  private static JedisPool jedisPool;

  static {
    PropertiesUtil properties = new PropertiesUtil("config/redis.properties");
    hostname = properties.readPropertyByKey("host");
    port = properties.readPropertyByKey("port");
    database = properties.readPropertyByKey("database");

    jedisPool = new JedisPool("redis://" + hostname + ":" + port + "/" + database);
  }

  public static long sadd(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.sadd(key, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static long scard(String key) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.scard(key);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static long srem(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.srem(key, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static boolean sismember(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.sismember(key, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return false;
  }

  public static long lpush(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.lpush(key, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static List<String> brpop(int timeout, String key) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.brpop(timeout, key);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }

  public static List<String> lrange(String key, int start, int end) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.lrange(key, start, end);
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }

  public static long lrem(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.lrem(key, 0, value);
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static long llen(String key) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.llen(key);
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static long hset(String key, String field, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.hset(key, field, value);
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static String hget(String key, String field) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.hget(key, field);
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }


  public static Jedis getJedis() {
    try {
      return jedisPool.getResource();
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    }

    return null;
  }

  public static Transaction multi(Jedis jedis) {
    try {
      return jedis.multi();
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    }

    return null;
  }

  public static List<Object> exec(Jedis jedis, Transaction tran) {
    try {

      return tran.exec();
    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (tran != null) {
        try {
          tran.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (jedis != null)
        jedis.close();
    }

    return null;
  }

  public static long zadd(String key, double score, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zadd(key, score, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static long zrem(String key, String value) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zrem(key, value);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static Set<String> zrange(String key, int start, int end) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zrange(key, start, end);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }

  public static Set<String> zrevrange(String key, int start, int end) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zrevrange(key, start, end);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }

  public static long zcard(String key) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zcard(key);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return 0;
  }

  public static Double zscore(String key, String member) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();

      return jedis.zscore(key, member);

    } catch (Exception e) {
      logger.error("出现异常" + e.getMessage());
    } finally {
      if (jedis != null)
        jedis.close();
    }

    return null;
  }
}
