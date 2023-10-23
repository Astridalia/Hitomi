package github.astridalia.database

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOneById
import java.util.*

open class MongoDBStorage<T : Any>(private val clazz: Class<T>) : Storage<T> {
    private val collection by lazy {
        val database = MongoManager.mongodbClient.getDatabase("Hitomi")
        database.getCollection(collectionName, clazz)
    }

    protected val collectionName: String = clazz.simpleName.lowercase(Locale.getDefault())

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        collection.updateOneById(id, entity, UpdateOptions().upsert(true))
    }

    override fun get(id: Id<T>): T? = collection.findOneById(id)

    override fun getAll(): List<T> = collection.find().toList()

    override fun remove(id: Id<T>) {
        collection.deleteOneById(id)
    }
}