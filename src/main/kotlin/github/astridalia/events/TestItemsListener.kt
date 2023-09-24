package github.astridalia.events

import github.astridalia.character.*
import github.astridalia.database.CachedMongoDBStorage
import github.astridalia.items.enchantments.CustomEnchantments
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.id.StringId
import java.util.*

class TestItemsListener : Listener, KoinComponent {

    private val customEnchantments: CustomEnchantments by inject()


    fun getLevelMultiplier(attackerLevel: Int, defenderLevel: Int): Double {
        val levelDifference = attackerLevel - defenderLevel

        return when {
            levelDifference >= 5 -> 2.0
            levelDifference >= 2 -> 1.5
            levelDifference >= -1 -> 1.0
            levelDifference >= -4 -> 0.5
            else -> 0.25
        }
    }

    fun calculateCharacterStats(
        baseStats: CharacterStats,
        elementModifiers: ElementModifier,
        physicalModifiers: PhysicalAttributeModifier,
        weaknesses: ElementalWeaknesses,
        astralRelations: AstralRelation
    ): CharacterStats {
        baseStats.intellect = (baseStats.intellect * elementModifiers.chaosElement).toInt()
        baseStats.agility = (baseStats.agility * elementModifiers.chaosElement).toInt()
        baseStats.will = (baseStats.will * elementModifiers.chaosElement).toInt()

        baseStats.strength = (baseStats.strength * elementModifiers.earthElement).toInt()
        baseStats.will = (baseStats.will * elementModifiers.earthElement).toInt()

        baseStats.will = (baseStats.will * elementModifiers.equilibriumElement).toInt()
        baseStats.power = (baseStats.power * elementModifiers.equilibriumElement).toInt()

        baseStats.agility = (baseStats.agility * elementModifiers.frostElement).toInt()
        baseStats.will = (baseStats.will * elementModifiers.frostElement).toInt()

        baseStats.intellect = (baseStats.intellect * elementModifiers.spiritElement).toInt()
        baseStats.will = (baseStats.will * elementModifiers.spiritElement).toInt()

        baseStats.strength = (baseStats.strength * elementModifiers.tempestElement).toInt()
        baseStats.agility = (baseStats.agility * elementModifiers.tempestElement).toInt()
        baseStats.intellect = (baseStats.intellect * elementModifiers.tempestElement).toInt()

        // Apply physical attribute modifiers
        baseStats.strength = (baseStats.strength * physicalModifiers.physical).toInt()
        baseStats.agility = (baseStats.agility * physicalModifiers.physical).toInt()

        baseStats.will = (baseStats.will * physicalModifiers.stamina).toInt()
        baseStats.agility = (baseStats.agility * physicalModifiers.stamina).toInt()

        baseStats.intellect = (baseStats.intellect * physicalModifiers.mana).toInt()
        baseStats.power = (baseStats.power * physicalModifiers.mana).toInt()

        baseStats.will = (baseStats.will * physicalModifiers.resists).toInt()
        baseStats.power = (baseStats.power * physicalModifiers.resists).toInt()

        baseStats.agility = (baseStats.agility * physicalModifiers.dodging).toInt()
        baseStats.will = (baseStats.will * physicalModifiers.dodging).toInt()

        // Apply elemental weaknesses
        baseStats.strength = (baseStats.strength * (1 - weaknesses.earthWeakness)).toInt()
        baseStats.intellect = (baseStats.intellect * (1 - weaknesses.chaosWeakness)).toInt()
        baseStats.agility = (baseStats.agility * (1 - weaknesses.frostWeakness)).toInt()
        baseStats.will = (baseStats.will * (1 - weaknesses.spiritWeakness)).toInt()
        baseStats.power = (baseStats.power * (1 - weaknesses.tempestWeakness)).toInt()
        baseStats.power = (baseStats.power * (1 - weaknesses.combustionWeakness)).toInt()

        // Apply Astral relations
        baseStats.will = (baseStats.will * astralRelations.starRelation).toInt()
        baseStats.intellect = (baseStats.intellect * astralRelations.sunRelation).toInt()

        return baseStats
    }

    private fun calculateStatsBetweenAttackerAndDefender(
        attacker: Profile,
        defender: Profile
    ): Pair<CharacterStats, CharacterStats> {
        val attackerStats = calculateCharacterStats(
            baseStats = attacker.stats,
            elementModifiers = attacker.elementModifier,
            physicalModifiers = attacker.physicalAttributeModifier,
            weaknesses = attacker.elementalWeaknesses,
            astralRelations = attacker.astralRelation
        )

        val defenderStats = calculateCharacterStats(
            baseStats = defender.stats,
            elementModifiers = defender.elementModifier,
            physicalModifiers = defender.physicalAttributeModifier,
            weaknesses = defender.elementalWeaknesses,
            astralRelations = defender.astralRelation
        )

        // You can also print or log the attacker and defender stats for debugging purposes
        println("Attacker Stats: $attackerStats")
        println("Defender Stats: $defenderStats")

        return attackerStats to defenderStats
    }

    @EventHandler
    fun onDamageBYEntity(e: EntityDamageByEntityEvent) {
        val attackerProfile = getProfileForEntity(e.damager) ?: return
        val defenderProfile = getProfileForEntity(e.entity) ?: return
        val (attackerStats, defenderStats) = calculateStatsBetweenAttackerAndDefender(attackerProfile, defenderProfile)
        e.damage = calculateDamage(attackerStats, defenderStats)
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private fun getProfileForEntity(entity: Entity): Profile? {
        val testStats = CachedMongoDBStorage(Profile::class.java, "players")
        val entityIdString = StringId<Profile>(entity.uniqueId.toString())
        return testStats.get(entityIdString)
    }

    private fun calculateDamage(attackerStats: CharacterStats, defenderStats: CharacterStats): Double {
        // You can implement your custom damage calculation logic here
        // For example, you can calculate damage based on the difference in strength of the attacker and defender
        val damage = (attackerStats.strength - defenderStats.strength).coerceAtLeast(0)

        // You can apply additional modifiers or formulas as needed
        // For example, you can multiply the damage by a scaling factor

        val levelMultiplier = getLevelMultiplier(attackerStats.level, defenderStats.level)

        return damage.toDouble() * levelMultiplier
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        // Ensure this event handler is only executed once per player join

            val testStats = CachedMongoDBStorage(Profile::class.java, "players")
            val playerIdString = StringId<Profile>(player.uniqueId.toString())
            val stats = testStats.get(playerIdString) ?: run {
                val itemEntity = Profile(player.uniqueId.toString(), CharacterStats())
                testStats.insertOrUpdate(playerIdString, itemEntity)
                itemEntity
            }
            println(stats)
        }
}