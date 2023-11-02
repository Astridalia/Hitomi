package github.astridalia.character

import kotlinx.serialization.Serializable


@Serializable
data class CharacterStats(
    var level: Int = 65,
    var xp: Double = 10.0,
    var balance: Int = 1000,
    var strength: Int = 12,
    var intellect: Int = 15,
    var agility: Int = 20,
    var will: Int = 25,
    var power: Int = 120
)