package github.astridalia.dynamics.items.enchantments.events

import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import org.bukkit.Color.RED
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object LodeListener : Listener {
    val lodeListener = SerializableEnchant(
        "Lode_Listener",
        "Enhances the user's ability to hear the faint echoes of valuable resources, helping them locate hidden treasures deep underground.",
        5
    )

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val itemInMainHand = player.inventory.itemInMainHand
        val lodeLevel = itemInMainHand.getEnchantOf(lodeListener)
        if (lodeLevel <= 0) return

        checkForValuableResources(player, 10.0, 5.0)
    }

    fun checkForValuableResources(player: Player, radius: Double, distanceForSound: Double) {
        val world = player.world
        val playerLocation = player.location

        for (degree in 0..<360) {
            val radian = Math.toRadians(degree.toDouble())
            val dx = (cos(radian) * radius).roundToInt()
            val dz = (sin(radian) * radius).roundToInt()

            val blockLocation = playerLocation.clone().add(dx.toDouble(), 0.0, dz.toDouble())
            val block = world.getBlockAt(blockLocation)

            if (block.type in valuableResources) {
                // Draw a line to the block
                player.spawnParticle(
                    Particle.REDSTONE,
                    blockLocation.x,
                    blockLocation.y,
                    blockLocation.z,
                    1,
                    Particle.DustOptions(RED, 1f)
                )

                // Play a sound if the player is within a certain distance
                if (playerLocation.distance(blockLocation) <= distanceForSound) {
                    player.playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
                }
            }
        }
    }

    val valuableResources = setOf(Material.DIAMOND_ORE, Material.GOLD_ORE, Material.IRON_ORE)
}