package github.astridalia.dynamics

import kotlinx.serialization.Serializable
import org.bukkit.event.inventory.InventoryType


@Serializable
data class SerializableDynamicInventory(
    override var title: String = "Inventory",
    override var size: Int = InventoryType.CHEST.defaultSize,
    override var items: MutableMap<Int, SerializableDynamicInventoryItem> = mutableMapOf(),
    override var isCancelled: Boolean = true,
) : IInventoryDynamics
