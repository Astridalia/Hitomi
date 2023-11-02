package github.astridalia.events

import github.astridalia.character.*

object MathCalculations {
    fun getLevelMultiplier(attackerLevel: Int, defenderLevel: Int): Double {
        return with(attackerLevel - defenderLevel) {
            when {
                this >= 5 -> 2.0
                this >= 2 -> 1.5
                this >= -1 -> 1.0
                this >= -4 -> 0.5
                else -> 0.25
            }
        }
    }

    private fun calculateStat(baseStat: Int, multiplier: Double): Int {
        return (baseStat * multiplier).toInt()
    }

    private fun calculateProfileStats(profile: Profile): CharacterStats {
        return calculateCharacterStats(
            baseStats = profile.stats,
            elementModifiers = profile.elementModifier,
            physicalModifiers = profile.physicalAttributeModifier,
            weaknesses = profile.elementalWeaknesses,
            astralRelations = profile.astralRelation
        )
    }

    fun calculateStatsBetweenAttackerAndDefender(
        attacker: Profile,
        defender: Profile
    ): Pair<CharacterStats, CharacterStats> {
        val attackerStats = calculateProfileStats(attacker)
        val defenderStats = calculateProfileStats(defender)
        return attackerStats to defenderStats
    }

    fun calculateDamage(attackerStats: CharacterStats, defenderStats: CharacterStats): Double {
        return with(attackerStats.strength - defenderStats.strength) {
            val damage = this.coerceAtLeast(0).toDouble()
            val levelMultiplier = getLevelMultiplier(attackerStats.level, defenderStats.level)
            damage * levelMultiplier
        }
    }

    fun calculateCharacterStats(
        baseStats: CharacterStats,
        elementModifiers: ElementModifier,
        physicalModifiers: PhysicalAttributeModifier,
        weaknesses: ElementalWeaknesses,
        astralRelations: AstralRelation
    ): CharacterStats {
        val intellectMultiplier =
            elementModifiers.chaosElement * elementModifiers.spiritElement * elementModifiers.tempestElement * physicalModifiers.mana * (1 - weaknesses.chaosWeakness) * astralRelations.sunRelation
        val agilityMultiplier =
            elementModifiers.chaosElement * elementModifiers.frostElement * elementModifiers.tempestElement * physicalModifiers.physical * physicalModifiers.stamina * physicalModifiers.dodging * (1 - weaknesses.frostWeakness)
        val willMultiplier =
            elementModifiers.chaosElement * elementModifiers.earthElement * elementModifiers.equilibriumElement * elementModifiers.frostElement * elementModifiers.spiritElement * physicalModifiers.stamina * physicalModifiers.resists * physicalModifiers.dodging * (1 - weaknesses.spiritWeakness) * astralRelations.starRelation
        val strengthMultiplier =
            elementModifiers.earthElement * elementModifiers.tempestElement * physicalModifiers.physical * (1 - weaknesses.earthWeakness)
        val powerMultiplier =
            elementModifiers.equilibriumElement * physicalModifiers.mana * physicalModifiers.resists * (1 - weaknesses.tempestWeakness) * (1 - weaknesses.combustionWeakness)

        // Create a new CharacterStats object and calculate the new stats
        return CharacterStats(
            intellect = calculateStat(baseStats.intellect, intellectMultiplier),
            agility = calculateStat(baseStats.agility, agilityMultiplier),
            will = calculateStat(baseStats.will, willMultiplier),
            strength = calculateStat(baseStats.strength, strengthMultiplier),
            power = calculateStat(baseStats.power, powerMultiplier)
        )
    }
}
