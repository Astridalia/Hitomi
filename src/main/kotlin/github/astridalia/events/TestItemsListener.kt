package github.astridalia.events

import github.astridalia.character.stats2.Character
import github.astridalia.character.stats2.MobGenerator
import github.astridalia.character.stats2.Stat
import github.astridalia.character.stats2.StatSystem
import github.astridalia.database.RedisCache
import github.astridalia.dynamics.inventories.SerializableDynamicInventory
import github.astridalia.dynamics.inventories.SerializableDynamicInventoryItem
import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.events.CustomEnchantEvent
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.litote.kmongo.id.StringId
import kotlin.random.Random

class TestItemsListener : Listener, KoinComponent {
    private val dynamicCharacter = RedisCache(Character::class.java)

    @EventHandler
    fun onCustomEnchantEvent(e: CustomEnchantEvent<SerializableEnchant>) {
        val item = e.enchantedItem
        e.player.sendMessage("You have used the ${item.name} enchantment at level ${item.level}!")
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val stringId = StringId<Character>(e.player.uniqueId.toString())
        val statSystem =  StatSystem()
        Stat.entries.forEach { statSystem.setBaseStat(it, 25) }
        dynamicCharacter.get(stringId) ?: dynamicCharacter.insertOrUpdate(stringId, Character(
            name = stringId.id,
            statSystem
        ))
    }

    @EventHandler
    fun onDamageEntity(e: EntityDamageByEntityEvent) {
        val playerUniqueId = e.damager.uniqueId.toString()
        val mobUniqueId = e.entity.uniqueId.toString()
        dynamicCharacter.get(StringId(mobUniqueId))?.let { mobCharacter ->
            dynamicCharacter.get(StringId(playerUniqueId))?.let { playerCharacter ->
                e.damage = calculateLinearDamage(playerCharacter, mobCharacter)
            }
        } ?: run {
            e.damage = calculateLinearDamage2(dynamicCharacter.get(StringId(playerUniqueId))!!, e.damage)
        }
        val defender = e.entity as? Player ?: return
        defender.sendMessage("You have been hit for ${e.damage} damage!")
    }


    private fun calculateLinearDamage2(player: Character, mobDamage: Double): Double {
        // Linear algorithm with weights for different stats
        val strengthWeight = 1.5
        val defenseWeight = 1.0
        val pierceWeight = 0.8
        val intellectWeight = 1.2
        val agilityWeight = 1.0
        val willWeight = 1.2

        val playerOffensivePower =
            strengthWeight * player.stats.getTotalStat(Stat.Strength) +
                    pierceWeight * player.stats.getTotalStat(Stat.Pierce) +
                    intellectWeight * player.stats.getTotalStat(Stat.Intellect) +
                    agilityWeight * player.stats.getTotalStat(Stat.Agility) +
                    willWeight * player.stats.getTotalStat(Stat.Will)

        val mobDefense =
            defenseWeight * mobDamage

        return (playerOffensivePower - mobDefense).coerceAtLeast(0.0)
    }

    @EventHandler
    fun onJoin2(e: PlayerJoinEvent) {
        val speed = e.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return
        speed.baseValue = calculateLinearStat(e.player.level, 0.5).toDouble()

        val attack = e.player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) ?: return
        attack.baseValue = calculateLinearStat(e.player.level, 160.0).toDouble()

        val defense = e.player.getAttribute(Attribute.GENERIC_ARMOR) ?: return
        defense.baseValue = calculateLinearStat(e.player.level, 70.0).toDouble()

        val health = e.player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        health.baseValue = calculateLinearStat(e.player.level, 100.0).toDouble()
    }

    private fun calculateLinearStat(playerLevel: Int, maxCap: Double): Int {
        // Linear formula: stat = level * coefficient
        val coefficient = maxCap / 50.0  // Adjust coefficient based on desired scaling
        val result = (playerLevel * coefficient).coerceAtMost(maxCap)
        return result.toInt()
    }
    private fun calculateLinearDamage(player: Character, mob: Character): Double {
        // Linear algorithm with weights for different stats
        val strengthWeight = 1.5
        val defenseWeight = 1.0
        val pierceWeight = 0.8
        val intellectWeight = 1.2
        val agilityWeight = 1.0
        val willWeight = 1.2

        val playerOffensivePower =
                    strengthWeight * player.stats.getTotalStat(Stat.Strength) +
                    pierceWeight * player.stats.getTotalStat(Stat.Pierce) +
                    intellectWeight * player.stats.getTotalStat(Stat.Intellect) +
                    agilityWeight * player.stats.getTotalStat(Stat.Agility) +
                    willWeight * player.stats.getTotalStat(Stat.Will)

        val mobDefense =
            defenseWeight * mob.stats.getTotalStat(Stat.Defense)

        return (playerOffensivePower - mobDefense).coerceAtLeast(0.0)
    }
}
