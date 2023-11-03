package github.astridalia.database

import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.changestream.ChangeStreamDocument
import github.astridalia.HitomiPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import org.litote.kmongo.updateOneById
import java.util.*

open class MongoDBStorage<T : Any>(private val entityClass: Class<T>) : Storage<T> {
    companion object {
        private const val DATABASE_NAME = "Hitomi"
    }

    private val collection by lazy {
        val database = MongoManager.mongodbClient.getDatabase(DATABASE_NAME)
        database.getCollection(collectionName, entityClass)
    }

    protected val collectionName: String = entityClass.simpleName.lowercase(Locale.getDefault())

    override fun insertOrUpdate(id: Id<T>, entity: T) {
        collection.updateOneById(id, entity, UpdateOptions().upsert(true))
    }

    override fun get(id: Id<T>): T? = collection.findOneById(id)

    override fun getAll(): List<T> = collection.find().toList()

    override fun remove(id: Id<T>) {
        collection.deleteOneById(id)
    }

    override fun listenForChanges(onChange: (ChangeStreamDocument<T>) -> Unit) {
        Thread {
            try {
                val changeStream = collection.watch(entityClass)
                val iterator = changeStream.iterator()
                while (iterator.hasNext()) {
                    val change = iterator.next()
                    val javaPlugin = JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java)
                    Bukkit.getScheduler().runTask(javaPlugin, Runnable { onChange(change) })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}