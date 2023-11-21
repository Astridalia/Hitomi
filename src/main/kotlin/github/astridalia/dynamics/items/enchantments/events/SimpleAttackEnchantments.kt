package github.astridalia.dynamics.items.enchantments.events


import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.component.KoinComponent

object SimpleAttackEnchantments : Listener, KoinComponent {
    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val damaged = e.damager as? Player ?: return
        val item = damaged.itemInHand

        checkAndExecuteEnchantment(item, e.entity, "charged") { entity, _ ->
            entity.location.world?.spawn(entity.location, LightningStrike::class.java)
        }

        checkAndExecuteEnchantment(item, e.entity, "fiery") { entity, value ->
            entity.fireTicks = (500L * value).toInt()
        }
    }

    @EventHandler
    fun onInteractCloaking(e: PlayerInteractEvent) {
        val item = e.item ?: return
        checkAndExecuteEnchantment(item, e.player, "cloaking") { _, level ->
            val potionEffect = PotionEffect(PotionEffectType.INVISIBILITY, 255 * level, 255, false, false)
            e.player.addPotionEffect(potionEffect)
        }
    }
}

fun checkAndExecuteEnchantment(
    item: ItemStack,
    player: Entity,
    enchantName: String,
    action: (Entity, Int) -> Unit
) {
    val enchant = SerializableEnchant.matches(enchantName) ?: return
    val enchantOf = item.getEnchantOf(enchant)
    if (enchantOf <= 0) return
    val event = CustomEnchantEvent(player, enchant, action)
    Bukkit.getPluginManager().callEvent(event)
    if (!event.isCancelled) {
        action(player, enchantOf)
    }
}

