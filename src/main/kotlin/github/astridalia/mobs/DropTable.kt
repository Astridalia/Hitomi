package github.astridalia.mobs

import github.astridalia.database.CachedMongoDBStorage
import github.astridalia.items.SerializedItemStack
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import org.litote.kmongo.id.StringId
import kotlin.random.Random


@Serializable
data class DropTable(val drops: MutableMap<Int, Double> = mutableMapOf()) {
    private val cumulativeChances: Map<Int, Double>

    init {
        // Ensure that the chances sum up to 100%
        val totalChance = drops.values.sumOf { it }
        require(totalChance <= 1.0) { "Total drop chances exceed 100%" }
        cumulativeChances = computeCumulativeChances()
    }

    private fun computeCumulativeChances(): Map<Int, Double> {
        var cumulativeChance = 0.0
        return drops.entries.associate { (id, rarity) ->
            cumulativeChance += rarity
            id to cumulativeChance
        }
    }

    fun roll(): SerializedItemStack? {
        val cachedMongoDBStorage = CachedMongoDBStorage(SerializedItemStack::class.java, "itemStacks")
        val randomValue = Random.nextDouble(0.0, 1.0)
        for ((id, cumulativeChance) in cumulativeChances) {
            if (randomValue <= cumulativeChance) {
                val stringId = StringId<SerializedItemStack>(id.toString())
                return cachedMongoDBStorage.get(stringId)
            }
        }
        return null
    }
}
