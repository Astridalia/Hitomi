package github.astridalia.dynamics.inventories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.event.inventory.InventoryType


@Serializable
data class SerializableDynamicInventory(
    @BsonId @SerialName("_id") override var title: String = "Inventory",
    override var size: Int = InventoryType.CHEST.defaultSize,
    override var items: MutableMap<Int, SerializableDynamicInventoryItem> = mutableMapOf(),
    override var isCancelled: Boolean = true,
) : IInventoryDynamics
