package github.astridalia.items.enchantments.events


import github.astridalia.items.enchantments.CustomEnchant
import github.astridalia.items.enchantments.getEnchantOf
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

object SimpleAttackEnchantments : Listener, KoinComponent {
    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val item = player.itemInHand

        val charged = item.getEnchantOf(CustomEnchant(name = "Charged")) ?: 0
        if (charged > 0) e.entity.location.world?.spawn(e.entity.location, LightningStrike::class.java)

        val fiery = item.getEnchantOf(CustomEnchant(name = "Fiery")) ?: 0
        if (fiery > 0) e.entity.fireTicks = (500L * fiery).toInt()
    }

    @EventHandler
    fun onInteractCloaking(e: PlayerInteractEvent) {
        val item = e.item ?: return
        val cloaking = item.getEnchantOf(CustomEnchant(name = "Cloaking")) ?: 0
        if (cloaking > 0 && (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.LEFT_CLICK_AIR)) {
            val potionEffect = PotionEffect(PotionEffectType.INVISIBILITY, 255 * cloaking, 255, false, false)
            e.player.addPotionEffect(potionEffect)
        }
    }

    @EventHandler
    fun onPlayerDeathSoulBound(e: PlayerDeathEvent) {
        e.entity.inventory.forEach {
            val soulBound = it.getEnchantOf(CustomEnchant(name = "Soulbound")) ?: 0
            if (soulBound > 0) {
                e.drops.remove(it)
                e.entity.inventory.addItem(it)
            }
        }
    }
}

