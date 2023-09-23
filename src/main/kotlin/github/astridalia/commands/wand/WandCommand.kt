package github.astridalia.commands.wand

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.max
import kotlin.math.min

@CommandAlias("wand")
@CommandPermission("hitomi.admin")
object WandCommand : BaseCommand(), Listener {
    private val wandItem = ItemStack(Material.BLAZE_ROD)
    private val playerRegionCorners = mutableMapOf<UUID, List<Location>>()
    private val protectedRegions = mutableMapOf<String, Pair<Location, Location>>()
    private val playerSelections = mutableMapOf<Player, Pair<Block?, Block?>>()
    private fun createProtectedRegion(player: Player, regionName: String) {
        val corner1 = playerRegionCorners[player.uniqueId]?.getOrNull(0)
        val corner2 = playerRegionCorners[player.uniqueId]?.getOrNull(1)

        if (corner1 != null && corner2 != null) {
            // Check if the region name already exists
            if (protectedRegions.containsKey(regionName)) {
                player.sendMessage("A region with that name already exists.")
                return
            }

            // Store the region corners
            protectedRegions[regionName] = Pair(corner1, corner2)
            player.sendMessage("Protected region '$regionName' created.")
        } else {
            player.sendMessage("You must set two corners to create a protected region.")
        }
    }

    private fun isLocationInRegion(location: Location): Boolean {
        for ((_, region) in protectedRegions) {
            val (corner1, corner2) = region
            if (isWithinCuboid(location, corner1, corner2)) {
                return true
            }
        }
        return false
    }

    private fun isWithinCuboid(location: Location, corner1: Location, corner2: Location): Boolean {
        val minX = min(corner1.blockX, corner2.blockX)
        val minY = min(corner1.blockY, corner2.blockY)
        val minZ = min(corner1.blockZ, corner2.blockZ)
        val maxX = max(corner1.blockX, corner2.blockX)
        val maxY = max(corner1.blockY, corner2.blockY)
        val maxZ = max(corner1.blockZ, corner2.blockZ)

        return location.blockX in minX..maxX &&
                location.blockY in minY..maxY &&
                location.blockZ in minZ..maxZ
    }

    private fun setBlockAt(location: Location, blockType: Material, blockData: BlockData?) {
        val world = location.world ?: return
        val block = world.getBlockAt(location)

        block.type = blockType
        if (blockData != null) {
            block.blockData = blockData
        }
    }

    private fun getRegion(corner1: Location, corner2: Location): List<Location> {
        val minX = min(corner1.blockX, corner2.blockX)
        val minY = min(corner1.blockY, corner2.blockY)
        val minZ = min(corner1.blockZ, corner2.blockZ)
        val maxX = max(corner1.blockX, corner2.blockX)
        val maxY = max(corner1.blockY, corner2.blockY)
        val maxZ = max(corner1.blockZ, corner2.blockZ)

        val locations = mutableListOf<Location>()
        val world = corner1.world

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    locations.add(Location(world, x.toDouble(), y.toDouble(), z.toDouble()))
                }
            }
        }

        return locations
    }


    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        // Check if the player is holding the protection wand
        if (player.inventory.itemInMainHand != wandItem) return
        println(playerSelections)
        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            // Right-click to select corners
            event.isCancelled = true
            val block = event.clickedBlock

            // Toggle between selecting the first and second corner
            val (corner1, corner2) = playerSelections.getOrDefault(player, null to null)
            if (corner1 == null) {
                playerSelections[player] = block to corner2
                player.sendMessage("First corner selected.")
            } else if (corner2 == null) {
                playerSelections[player] = corner1 to block
                player.sendMessage("Second corner selected.")
            }
        } else if (event.action == Action.LEFT_CLICK_BLOCK) {
            // Left-click to clear selections
            event.isCancelled = true
            playerSelections[player] = null to null
            player.sendMessage("Selections cleared.")
        }
    }
}