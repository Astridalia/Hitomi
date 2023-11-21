package github.astridalia.dynamics.items.enchantments.events

import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object AutoSmelting : Listener {
    private const val AUTO_SMELTING_ENCHANT_NAME = "AutoSmelting"
    val customEnchant = SerializableEnchant(AUTO_SMELTING_ENCHANT_NAME, level = 1)

    private val smeltableMaterials = mutableMapOf(
        Material.IRON_ORE to Material.IRON_INGOT,
        Material.GOLD_ORE to Material.GOLD_INGOT,
        Material.COPPER_ORE to Material.COPPER_INGOT,
        Material.DEEPSLATE_COPPER_ORE to Material.COPPER_INGOT,
        Material.ANCIENT_DEBRIS to Material.NETHERITE_SCRAP,
        // Add more ores here
        Material.DEEPSLATE_IRON_ORE to Material.IRON_INGOT,
        Material.DEEPSLATE_GOLD_ORE to Material.GOLD_INGOT,
        Material.NETHER_GOLD_ORE to Material.GOLD_INGOT,
        Material.NETHER_QUARTZ_ORE to Material.QUARTZ
    )

    private fun Material.asItemStack(amount: Int = 1) = org.bukkit.inventory.ItemStack(this, amount)

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        checkAndExecuteEnchantment(event.player.inventory.itemInMainHand, event.player, AUTO_SMELTING_ENCHANT_NAME) { _, _ ->
            smeltableMaterials[event.block.type]?.let { smelted ->
                event.isCancelled = true
                event.block.type = Material.AIR
                event.block.world.dropItemNaturally(event.block.location, smelted.asItemStack())
            }
        }
    }
}