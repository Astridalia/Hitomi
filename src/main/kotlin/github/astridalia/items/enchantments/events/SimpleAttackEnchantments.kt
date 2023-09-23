package github.astridalia.items.enchantments.events


import github.astridalia.items.enchantments.CustomEnchantments
import github.astridalia.items.enchantments.HyperionEnchantments
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SimpleAttackEnchantments : Listener, KoinComponent {
    private val customEnchantments: CustomEnchantments by inject()

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val item = player.itemInHand

        val charged = customEnchantments.getFrom(item, HyperionEnchantments.CHARGED)
        if (charged > 0) {
            e.entity.location.world?.spawn(e.entity.location, LightningStrike::class.java)
        }

        val fiery = customEnchantments.getFrom(item, HyperionEnchantments.FIERY)
        if (fiery > 0) {
            e.entity.fireTicks = (500L * fiery).toInt()
        }
    }

    @EventHandler
    fun onInteractCloaking(e: PlayerInteractEvent) {
        val item = e.item ?: return
        val cloaking = customEnchantments.getFrom(item, HyperionEnchantments.CLOAKING)
        if (cloaking > 0 && (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.LEFT_CLICK_AIR)) {
            val potionEffect = PotionEffect(PotionEffectType.INVISIBILITY, 255 * cloaking, 255, false, false)
            e.player.addPotionEffect(potionEffect)
        }
    }

    @EventHandler
    fun onPlayerDeathSoulBound(e: PlayerDeathEvent) {
        e.entity.inventory.forEach {
            val soulBound = customEnchantments.getFrom(it, HyperionEnchantments.SOULBOUND)
            if (soulBound > 0) {
                e.drops.remove(it)
                e.entity.inventory.addItem(it)
            }
        }
    }
}

