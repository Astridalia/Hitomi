package github.astridalia.database

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.hash.Hashing
import org.litote.kmongo.Id
import org.litote.kmongo.id.StringId
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class RedisCache<T : Any>(
    private val clazz: Class<T>,
    private val collectName: String,
    private val cacheExpirationMinutes: Long = 30,
) : Storage<T>, MongoDBStorage<T>(clazz, collectName) {

    private val jedisPool = JedisPool(JedisPoolConfig(), "localhost")
    private val objectMapper = jacksonObjectMapper()

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        super.insertOrUpdate(id, entity)
        setCache(id.toHash(), entity)
    }

    private fun Id<T>.toHash(): StringId<T> {
        val hashing = Hashing.sha256().hashString("$this:$collectName", Charsets.UTF_8)
        return StringId(hashing.toString())
    }

    override fun get(id: Id<T>): T? {
        val cachedData = getCache(id.toHash())
        if (cachedData != null) {
            return cachedData
        }
        val dataFromDB = super.get(id)
        if (dataFromDB != null) {
            setCache(id.toHash(), dataFromDB)
        }
        return dataFromDB
    }

    override fun remove(id: Id<T>) {
        super.remove(id)
        removeCache(id.toHash())
    }

    private fun getCache(id: Id<T>): T? {
        jedisPool.resource.use { jedis ->
            val cachedData = jedis.get(id.toHash().id)
            return if (cachedData != null) {
                deserialize(cachedData, clazz) // Pass clazz parameter here
            } else {
                null
            }
        }
    }

    private fun setCache(id: Id<T>, entity: T) {
        jedisPool.resource.use { jedis ->
            jedis.setex(id.toString(), (cacheExpirationMinutes * 60).toInt(), serialize(entity))
        }
    }

    private fun removeCache(id: Id<T>) {
        jedisPool.resource.use { jedis ->
            jedis.del(id.toString())
        }
    }

    private fun serialize(entity: T): String {
        return objectMapper.writeValueAsString(entity)
    }

    private fun deserialize(data: String, clazz: Class<T>): T? {
        return objectMapper.readValue(data, clazz)
    }


}