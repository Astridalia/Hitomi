package github.astridalia.mobs

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.util.*

object MobManager {
    private val mutableListOfPool = mutableListOf<Entity>()

    private val entityClassMap: Map<String, Class<out Entity>?> = mapOf(
        "ZOMBIE" to EntityType.ZOMBIE.entityClass,
        "SKELETON" to EntityType.SKELETON.entityClass,
        "CREEPER" to EntityType.CREEPER.entityClass,
        "SPIDER" to EntityType.SPIDER.entityClass,
        "COW" to EntityType.COW.entityClass,
        "PIG" to EntityType.PIG.entityClass,
        "SHEEP" to EntityType.SHEEP.entityClass,
        "CHICKEN" to EntityType.CHICKEN.entityClass,
        "SQUID" to EntityType.SQUID.entityClass,
        "WOLF" to EntityType.WOLF.entityClass,
        "OCELOT" to EntityType.OCELOT.entityClass,
        "HORSE" to EntityType.HORSE.entityClass,
        "VILLAGER" to EntityType.VILLAGER.entityClass,
    )

    fun cleanUp() {
        mutableListOfPool.forEach(Entity::remove)
        mutableListOfPool.clear()
    }

    fun spawnCustomMob(location: Location, entityType: String, customName: String): Entity? {
        val entityClass = entityClassMap[entityType.uppercase(Locale.getDefault())] ?: return null
        val entity = location.world?.spawn(location, entityClass) { spawnedEntity ->
            spawnedEntity.isCustomNameVisible = true
            spawnedEntity.customName = customName
        }
        entity?.let(mutableListOfPool::add)
        return entity
    }
}