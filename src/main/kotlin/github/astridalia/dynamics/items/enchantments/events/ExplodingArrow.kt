package github.astridalia.dynamics.items.enchantments.events


import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.koin.core.component.KoinComponent

object ExplodingArrow : Listener, KoinComponent {
    private const val EXPLOSION_POWER = 2.5f
    private const val EXPLODING_ARROWS_ENCHANT_NAME = "Exploding_Arrows"
    private val arrowShooters: MutableMap<Arrow, Player> = HashMap()
    private val customEnchant = SerializableEnchant(EXPLODING_ARROWS_ENCHANT_NAME, level = 1)

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        try {
            val shooter = event.entity as? Player ?: return
            val bow = event.bow ?: return

            bow.getEnchantOf(customEnchant).let { enchantment ->
                if (enchantment > 0) {
                    (event.projectile as? Arrow)?.let { arrow ->
                        arrowShooters[arrow] = shooter
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onProjectileHit(event: ProjectileHitEvent) {
        try {
            val arrow = event.entity as? Arrow ?: return
            val shooter = arrowShooters.remove(arrow) ?: return

            shooter.inventory.itemInMainHand.getEnchantOf(customEnchant).let { explodingLevel ->
                if (explodingLevel > 0) {
                    explodeArrow(arrow, EXPLOSION_POWER * explodingLevel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun explodeArrow(arrow: Arrow, explosionPower: Float) {
        val world = arrow.location.world ?: return

        // Create explosion
        world.createExplosion(arrow.location, explosionPower)

        // Spawn particle animation along the arrow trail
        val particleCount = 100
        repeat(particleCount) {
            // Calculate random offsets for particle spawning
            val offsetX = (Math.random() - 0.5) * 2.0
            val offsetY = (Math.random() - 0.5) * 2.0
            val offsetZ = (Math.random() - 0.5) * 2.0

            // Calculate the position slightly ahead of the arrow's position
            val spawnLocation = arrow.location.clone().add(arrow.velocity.normalize().multiply(it * 0.1))

            // Spawn the particle at the calculated location with random offsets
            world.spawnParticle(
                Particle.FLAME,
                spawnLocation.x + offsetX,
                spawnLocation.y + offsetY,
                spawnLocation.z + offsetZ,
                1
            )
        }

        arrow.remove()
    }
}