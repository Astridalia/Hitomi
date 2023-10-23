package github.astridalia.items.enchantments.events


import github.astridalia.items.enchantments.CustomEnchant
import github.astridalia.items.enchantments.getEnchantOf
import org.bukkit.Bukkit
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
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

        val charged = item.getEnchantOf(CustomEnchant("Charged")) ?: 0
        if (charged > 0) e.entity.location.world?.spawn(e.entity.location, LightningStrike::class.java)

        val fiery = item.getEnchantOf(CustomEnchant("Fiery")) ?: 0
        if (fiery > 0) e.entity.fireTicks = (500L * fiery).toInt()
    }

    @EventHandler
    fun onInteractCloaking(e: PlayerInteractEvent) {
        val item = e.item ?: return
        val cloaking = item.getEnchantOf(CustomEnchant("Cloaking")) ?: 0
        if (cloaking > 0 && (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.LEFT_CLICK_AIR)) {
            val potionEffect = PotionEffect(PotionEffectType.INVISIBILITY, 255 * cloaking, 255, false, false)
            e.player.addPotionEffect(potionEffect)
        }
    }

    // TODO: Soulbound is broken?
    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        val inventory = player.inventory.contents.toList()
        val armor = player.inventory.armorContents.toList()

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.inventory.armorContents = arrayOfNulls<ItemStack>(4)

            inventory.forEach {
                if (it != null && (it.getEnchantOf(CustomEnchant("Soulbound")) ?: 0) > 0) {
                    player.inventory.addItem(it)
                }
            }

            armor.forEachIndexed { index, itemStack ->
                if (itemStack != null && (itemStack.getEnchantOf(CustomEnchant("Soulbound")) ?: 0) > 0) {
                    player.inventory.setItem(index, itemStack)
                }
            }
        }, 1L)
    }
}

