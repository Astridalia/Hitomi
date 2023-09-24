package github.astridalia.database

import com.github.benmanes.caffeine.cache.Caffeine
import org.litote.kmongo.Id
import java.util.concurrent.TimeUnit

class CachedMongoDBStorage<T : Any>(
    clazz: Class<T>,
    collectName: String,
    cacheExpirationMinutes: Long = 30,
    maxCacheSize: Long = 1000
) : Storage<T>, MongoDBStorage<T>(clazz, collectName) {

    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(cacheExpirationMinutes, TimeUnit.MINUTES)
        .maximumSize(maxCacheSize)
        .build<Id<T>, T>()

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        cache.put(id, entity)
        super.insertOrUpdate(id, entity)
    }

    override fun get(id: Id<T>): T? = cache.get(id) { super.get(id) }

    override fun remove(id: Id<T>) {
        cache.invalidate(id)
        super.remove(id)
    }

    fun clearCache() = cache.invalidateAll()
}
