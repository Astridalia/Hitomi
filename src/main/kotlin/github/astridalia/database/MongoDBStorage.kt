package github.astridalia.database


import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOneById

open class MongoDBStorage<T : Any>(clazz: Class<T>, collectName: String) : Storage<T> {
    private val collection: MongoCollection<T> by lazy {
        val mongodbClient = MongoManager.mongodbClient
        val database: MongoDatabase = mongodbClient.getDatabase("Hitomi")
        database.getCollection(collectName, clazz)
    }

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        collection.updateOneById(id, entity, UpdateOptions().upsert(true))
    }

    override fun get(id: Id<T>): T? = collection.findOneById(id)

    override fun getAll(): List<T> = collection.find().toList()

    override fun remove(id: Id<T>) {
        collection.deleteOneById(id)
    }
}