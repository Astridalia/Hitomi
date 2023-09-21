package github.astridalia.character

data class CharacterStats(
    var level: Int = 1,
    var xp: Double = 0.0,
    var balance: Int = 0,
    var strength: Int = 0,
    var intellect: Int = 0,
    var agility: Int = 0,
    var will: Int = 0,
    var power: Int = 0
)