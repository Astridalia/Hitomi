package github.astridalia.character.stats2

import kotlinx.serialization.SerialName
import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID
import kotlin.random.Random

data class MobGenerator(
    @BsonId @SerialName("_id")
    var name: String = UUID.randomUUID().toString(),
                   private val minBaseStat: Int, private val maxBaseStat: Int,
                   private val minBonusStat: Int, private val maxBonusStat: Int) {

    // Generate random stats for a mob based on player level.
    fun generateMobStats(playerLevel: Int): StatSystem {
        val mobStats = StatSystem()

        // Set base stats within the specified range
        mobStats.setBaseStat(Stat.Strength, Random.nextInt(minBaseStat, maxBaseStat + 1))
        mobStats.setBaseStat(Stat.Defense, Random.nextInt(minBaseStat, maxBaseStat + 1))
        mobStats.setBaseStat(Stat.Pierce, Random.nextInt(minBaseStat, maxBaseStat + 1))

        // Add bonus stats within the specified range
        mobStats.addBonusStat(Stat.Strength, Random.nextInt(minBonusStat, maxBonusStat + 1))
        mobStats.addBonusStat(Stat.Defense, Random.nextInt(minBonusStat, maxBonusStat + 1))
        mobStats.addBonusStat(Stat.Pierce, Random.nextInt(minBonusStat, maxBonusStat + 1))

        // Adjust stats based on player level
        val levelMultiplier = playerLevel.toDouble() / 10.0
        for (stat in Stat.Strength.ordinal..Stat.Pierce.ordinal) {
            val currentStat = Stat.entries[stat]  // Use Stat.values() instead of Stat.entries
            mobStats.setBaseStat(currentStat, (mobStats.getTotalStat(currentStat) * levelMultiplier).toInt())
        }
        return mobStats
    }

}