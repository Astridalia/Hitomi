package github.astridalia.dynamics.inventories.chests

import github.astridalia.dynamics.inventories.SerializableDynamicInventoryItem
import github.astridalia.modules.serializers.LocationSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.event.inventory.InventoryType

@Serializable
data class DynamicLocationChest(
    @Serializable(with = LocationSerializer::class)
    override var location: Location,
    override var cooldown: Long = 0L,
    override var lastOpened: Long = 0L,
    override var title: String = "Tier #1",
    override var size: Int = InventoryType.CHEST.defaultSize,
    override var items: MutableMap<Int, SerializableDynamicInventoryItem> = mutableMapOf(),
    override var isCancelled: Boolean = false,
) : IChestLocation