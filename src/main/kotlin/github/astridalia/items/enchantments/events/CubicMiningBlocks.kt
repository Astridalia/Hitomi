package github.astridalia.items.enchantments.events

import github.astridalia.items.enchantments.CustomEnchantments
import github.astridalia.items.enchantments.HyperionEnchantments
import kotlinx.coroutines.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CubicMiningBlocks : KoinComponent, Listener {
    private val customEnchantments: CustomEnchantments by inject()

    private val unbreakableMaterials = setOf(
        Material.BEDROCK, Material.BARRIER, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
        Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID
    )

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        // Check if the player has the Cubic Mining enchantment
        val itemInMainHand = player.inventory.itemInMainHand
        val cubicMiningLevel = customEnchantments.getFrom(itemInMainHand, HyperionEnchantments.GRID_PICKAXE)
        if (cubicMiningLevel <= 0) return

        val cubeBlocks = getCubicBlocks(block)
        breakCubicBlocks(player, cubeBlocks)
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
            cubeBlock.breakNaturally()
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