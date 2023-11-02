package github.astridalia.dynamics.inventories

import github.astridalia.dynamics.CustomDynamicActions
import github.astridalia.dynamics.items.IGUIActionItem
import github.astridalia.modules.serializers.MaterialSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Material


@Serializable
data class SerializableDynamicInventoryItem(
    @Serializable(with = MaterialSerializer::class)
    override var type: Material,
    override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(),
    override var model: Int = 0,
    @Serializable(with = InventoryActionSerializer::class)
    override var action: CustomDynamicActions = CustomDynamicActions.NONE
) : IGUIActionItem