package net.frozenorb.potpvp.kt.redis

import redis.clients.jedis.Jedis

interface RedisCommand<T> {
    fun execute(p0: Jedis?): T
}