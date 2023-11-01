package github.astridalia.mobs

import github.astridalia.database.RedisCache
import github.astridalia.dynamics.SerializableDynamicItem
import kotlinx.serialization.Serializable
import org.litote.kmongo.id.StringId
import kotlin.random.Random


@Serializable
data class DropTable(val drops: MutableMap<Int, Double> = mutableMapOf()) {
    private var cumulativeChances: Map<Int, Double>

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

    fun roll(): SerializableDynamicItem? {
        val cachedMongoDBStorage = RedisCache(SerializableDynamicItem::class.java)
        val randomValue = Random.nextDouble(0.0, 1.0)
        return cumulativeChances.entries
            .firstOrNull { (_, cumulativeChance) -> randomValue <= cumulativeChance }
            ?.let { (id, _) ->
                val stringId = StringId<SerializableDynamicItem>(id.toString())
                cachedMongoDBStorage.get(stringId)
            }
    }

    init {
        require(drops.isNotEmpty()) { "Drop table must not be empty" }
        cumulativeChances = computeCumulativeChances().toMap()
    }
}