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
        mutableListOfPool.removeIf { !it.isValid }
    }

    fun spawnCustomMob(location: Location, entityType: String, customName: String): Entity? {
        val entityClass = entityClassMap[entityType.uppercase(Locale.getDefault())] ?: return null
        val entity = location.world?.spawn(location, entityClass)
        entity?.let {
            val setCustomNameVisibleMethod =
                it::class.java.getDeclaredMethod("setCustomNameVisible", Boolean::class.java)
            val setCustomNameMethod = it::class.java.getDeclaredMethod("setCustomName", String::class.java)
            setCustomNameVisibleMethod.invoke(it, true)
            setCustomNameMethod.invoke(it, customName)
            mutableListOfPool.add(it)
        }
        return entity
    }
}