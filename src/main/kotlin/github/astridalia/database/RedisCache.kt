package github.astridalia.database

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.hash.Hashing
import org.litote.kmongo.Id
import org.litote.kmongo.id.StringId
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class RedisCache<T : Any>(
    private val clazz: Class<T>,
    private val cacheExpirationMinutes: Long = 30,
) : Storage<T>, MongoDBStorage<T>(clazz) {

    private val jedisPool = JedisPool(JedisPoolConfig(), "localhost")
    private val objectMapper = jacksonObjectMapper()

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        super.insertOrUpdate(id, entity)
        setCache(id.toHash(), entity)
    }

    override fun get(id: Id<T>): T? {
        return getCache(id.toHash()) ?: super.get(id)?.also { setCache(id.toHash(), it) }
    }

    override fun remove(id: Id<T>) {
        super.remove(id)
        removeCache(id.toHash())
    }

    private fun Id<T>.toHash(): StringId<T> {
        val hashing = Hashing.sha256().hashString("$this:$collectionName", Charsets.UTF_8)
        return StringId(hashing.toString())
    }

    private fun getCache(id: Id<T>): T? = jedisPool.resource.use {
        it.get(id.toHash().id)?.let { deserialize(it, clazz) }
    }

    private fun setCache(id: Id<T>, entity: T) {
        jedisPool.resource.use {
            it.setex(id.toString(), (cacheExpirationMinutes * 60).toInt(), serialize(entity))
        }
    }

    private fun removeCache(id: Id<T>) {
        jedisPool.resource.use { it.del(id.toString()) }
    }

    private fun serialize(entity: T): String = objectMapper.writeValueAsString(entity)

    private fun deserialize(data: String, clazz: Class<T>): T? = objectMapper.readValue(data, clazz)
}