package github.astridalia.items.enchantments.events


import github.astridalia.items.enchantments.CustomEnchantments
import github.astridalia.items.enchantments.HyperionEnchantments
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ExplodingArrow : Listener, KoinComponent {
    private const val power = 2.5f
    private val arrowShooters: MutableMap<Arrow, Player> = HashMap()

    private val customEnchantments: CustomEnchantments by inject()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        val shooter = event.entity as? Player ?: return
        val bow = event.bow ?: return
        if (customEnchantments.getFrom(bow, HyperionEnchantments.EXPLODING_ARROW) <= 0) return
        val arrow = event.projectile as? Arrow ?: return
        arrowShooters[arrow] = shooter
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onProjectileHit(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return
        val shooter = arrowShooters[arrow] ?: return
        arrowShooters.remove(arrow)
        val explodingLevel =
            customEnchantments.getFrom(shooter.inventory.itemInMainHand, HyperionEnchantments.EXPLODING_ARROW)
        if (explodingLevel <= 0) return
        explodeArrow(arrow, power * explodingLevel)
    }

    private fun explodeArrow(arrow: Arrow, explosionPower: Float) {
        val world = arrow.location.world ?: return

        // Create explosion
        world.createExplosion(arrow.location, explosionPower)

        // Spawn particle animation along the arrow trail
        val particleCount = 100
        for (i in 0..<particleCount) {
            // Calculate random offsets for particle spawning
            val offsetX = (Math.random() - 0.5) * 2.0
            val offsetY = (Math.random() - 0.5) * 2.0
            val offsetZ = (Math.random() - 0.5) * 2.0

            // Calculate the position slightly ahead of the arrow's position
            val spawnLocation = arrow.location.clone().add(arrow.velocity.normalize().multiply(i * 0.1))

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
