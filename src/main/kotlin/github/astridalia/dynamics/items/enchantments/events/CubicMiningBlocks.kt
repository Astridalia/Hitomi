package github.astridalia.dynamics.items.enchantments.events

import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject

object CubicMiningBlocks : KoinComponent, Listener {
    private const val `ROCK SHAPER_ENCHANT_NAME` = "Rockshaper"
    private val scope = CoroutineScope(Dispatchers.Default)

    private val javaPlugin: JavaPlugin by inject(JavaPlugin::class.java)
    private val customEnchant = SerializableEnchant(
        `ROCK SHAPER_ENCHANT_NAME`,
        rarity = 000000000.125,
        level = 1,
        description = "Grants the ability to manipulate stone and terrain, aiding in tunneling and construction"
    )

    private val unbreakableMaterials = setOf(
        Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
        Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID
    )

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        try {
            val player = event.player
            val block = event.block

            // Check if the player has the Cubic Mining enchantment
            val itemInMainHand = player.inventory.itemInMainHand
            val cubicMiningLevel = itemInMainHand.getEnchantOf(customEnchant)
            if (cubicMiningLevel <= 0) return

            // Check if the player has the Auto Smelting enchantment
            val autoSmeltLevel = itemInMainHand.getEnchantOf(AutoSmelting.customEnchant)
            if (autoSmeltLevel > 0) AutoSmelting.onBlockBreak(event)

            val cubeBlocks = getCubicBlocks(block)

            // Delay the execution of breakCubicBlocks
            scope.launch {
                Bukkit.getScheduler().runTask(javaPlugin, Runnable { // Switch back to the main thread
                    breakCubicBlocks(player, cubeBlocks)
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun breakCubicBlocks(player: Player, cubeBlocks: List<Block>) {
        for (cubeBlock in cubeBlocks) {
            // Play break particles for each block in the cube
            player.world.spawnParticle(
                Particle.BLOCK_CRACK,
                cubeBlock.x + 0.5, cubeBlock.y + 0.5, cubeBlock.z + 0.5,
                100,
                0.0, 0.0, 0.0,
                0.1,
                cubeBlock.blockData
            )

            // Create a new BlockBreakEvent for the cubeBlock
            val event = BlockBreakEvent(cubeBlock, player)

            // Apply AutoSmelting if the player has the enchantment
            val itemInMainHand = player.inventory.itemInMainHand
            val autoSmeltLevel = itemInMainHand.getEnchantOf(AutoSmelting.customEnchant) ?: 0
            if (autoSmeltLevel > 0) AutoSmelting.onBlockBreak(event)

            // If the event was not cancelled by AutoSmelting, break the block naturally
            if (!event.isCancelled) cubeBlock.breakNaturally()
        }
    }

    private fun getCubicBlocks(centerBlock: Block): List<Block> {
        val cubeBlocks = mutableListOf<Block>()
        for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
            if (dx == 0 && dy == 0 && dz == 0) continue // Skip the center block
            val block = centerBlock.world.getBlockAt(centerBlock.x + dx, centerBlock.y + dy, centerBlock.z + dz)
            if (!block.type.isAir && !isUnbreakable(block.type)) cubeBlocks.add(block)
        }
        return cubeBlocks
    }

    private fun isUnbreakable(material: Material): Boolean {
        return material in unbreakableMaterials
    }
}