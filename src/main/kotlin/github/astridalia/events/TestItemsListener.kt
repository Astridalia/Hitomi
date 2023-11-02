package github.astridalia.events

import github.astridalia.character.CharacterStats
import github.astridalia.character.Profile
import github.astridalia.database.RedisCache
import github.astridalia.dynamics.items.SerializableDynamicItem
import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.events.MathCalculations.calculateStatsBetweenAttackerAndDefender
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.litote.kmongo.id.StringId

class TestItemsListener : Listener, KoinComponent {

    private fun calculateEventDamage(e: EntityDamageByEntityEvent): Double? {
        val attackerProfile = getProfileForEntity(e.damager) ?: return null
        val defenderProfile = getProfileForEntity(e.entity) ?: return null
        val calculateStatsBetweenAttackerAndDefender =
            calculateStatsBetweenAttackerAndDefender(attackerProfile, defenderProfile)
        val (attackerStats, defenderStats) = calculateStatsBetweenAttackerAndDefender
        return MathCalculations.calculateDamage(attackerStats, defenderStats)
    }

    @EventHandler
    fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        e.damage = calculateEventDamage(e) ?: return
    }

    private fun getProfileForEntity(entity: Entity): Profile? {
        val testStats = RedisCache(Profile::class.java)
        return testStats.get(StringId(entity.uniqueId.toString()))
    }



    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        val testStats = RedisCache(Profile::class.java)
        val playerIdString = StringId<Profile>(player.uniqueId.toString())
        testStats.get(playerIdString) ?: run {
            val itemEntity = Profile(player.uniqueId.toString(), CharacterStats())
            testStats.insertOrUpdate(playerIdString, itemEntity)
            itemEntity
        }

        val newEnchantSerializer = RedisCache(SerializableEnchant::class.java)
        val enchantId = StringId<SerializableEnchant>("fiery")
        newEnchantSerializer.get(enchantId) ?: run {
            val enchantEntity = SerializableEnchant(
                name = "fiery",
                level = 1,
                maxLevel = 5,
                description = "Sets the target on fire for a period of time."
            )
            newEnchantSerializer.insertOrUpdate(enchantId, enchantEntity)
            enchantEntity
        }

        // TODO: adding in more upgrades soon!
        val dynamicItem = RedisCache(SerializableDynamicItem::class.java)
        val stringId = StringId<SerializableDynamicItem>("test")
        val item = dynamicItem.get(stringId) ?: run {
            val itemEntity = SerializableDynamicItem(
                type = "DIAMOND_SWORD",
                name = "test_item",
                lore = mutableListOf(),
                model = 0
            )
            dynamicItem.insertOrUpdate(stringId, itemEntity)
            itemEntity
        }
        player.inventory.addItem(item.toItemStack())
    }


}