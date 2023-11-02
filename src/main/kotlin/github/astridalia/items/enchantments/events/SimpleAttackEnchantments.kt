package github.astridalia.items.enchantments.events


import github.astridalia.items.enchantments.CustomEnchant
import github.astridalia.items.enchantments.getEnchantOf
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SimpleAttackEnchantments : Listener, KoinComponent {

    private val plugin: JavaPlugin by inject()

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val item = player.itemInHand

        item.getEnchantOf(CustomEnchant("Charged"))?.let { charged ->
            if (charged > 0) e.entity.location.world?.spawn(e.entity.location, LightningStrike::class.java)
        }

        item.getEnchantOf(CustomEnchant("Fiery"))?.let { fiery ->
            if (fiery > 0) e.entity.fireTicks = (500L * fiery).toInt()
        }
    }

    @EventHandler
    fun onInteractCloaking(e: PlayerInteractEvent) {
        val item = e.item ?: return

        item.getEnchantOf(CustomEnchant("Cloaking"))?.let { cloaking ->
            when (e.action) {
                Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_AIR -> {
                    if (cloaking > 0) {
                        val potionEffect =
                            PotionEffect(PotionEffectType.INVISIBILITY, 255 * cloaking, 255, false, false)
                        e.player.addPotionEffect(potionEffect)
                    }
                }

                else -> { /* Do nothing */
                }
            }
        }
    }
}

