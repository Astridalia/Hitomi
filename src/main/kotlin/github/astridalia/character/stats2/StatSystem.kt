package github.astridalia.character.stats2

import kotlinx.serialization.Serializable


@Serializable
data class StatSystem(
    var baseStats: MutableMap<Stat, Int> = mutableMapOf(),
    var bonusStats: MutableMap<Stat, Int> = mutableMapOf()
) {
    // Set base value for a given statistic.
    fun setBaseStat(stat: Stat, value: Int) {
        baseStats[stat] = value
    }

    // Add bonus value to a given statistic.
    fun addBonusStat(stat: Stat, bonus: Int) {
        bonusStats[stat] = bonusStats.getOrDefault(stat, 0) + bonus
    }

    // Calculate total value for a given statistic.
    fun getTotalStat(stat: Stat): Int {
        return baseStats.getOrDefault(stat, 0) + bonusStats.getOrDefault(stat, 0)
    }
}