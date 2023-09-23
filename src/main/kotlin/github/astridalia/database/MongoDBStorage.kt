package github.astridalia.database


import com.mongodb.MongoClientSettings.builder
import com.mongodb.client.model.UpdateOptions
import org.bson.UuidRepresentation
import org.litote.kmongo.*

open class MongoDBStorage<T : Any>(clazz: Class<T>, collectName: String) : Storage<T> {
    private val mongodbClientSettings = builder().uuidRepresentation(UuidRepresentation.STANDARD)
        .build()

    private val mongodbClient = KMongo.createClient(mongodbClientSettings)
    private val database = mongodbClient.getDatabase("Hitomi")
    private val collection = database.getCollection(collectName, clazz)

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        collection.updateOneById(id, entity, UpdateOptions().apply { upsert(true) })
    }

    override fun get(id: Id<T>): T? {
        return collection.findOneById(id)
    }

    override fun getAll(): List<T> {
        return collection.find().toList()
    }

    override fun remove(id: Id<T>) {
        collection.deleteOneById(id)
    }
}