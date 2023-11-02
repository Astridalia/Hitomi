package github.astridalia.dynamics.inventories.chests

import github.astridalia.dynamics.inventories.IInventoryDynamics
import org.bukkit.Location

interface IChestLocation : IInventoryDynamics {
    var location: Location
    var cooldown: Long
    var lastOpened: Long
}