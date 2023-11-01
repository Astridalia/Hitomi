package github.astridalia.dynamics

import kotlinx.serialization.Serializable


@Serializable
data class SerializableDynamicInventoryItem(
    override var type: String,
    override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(),
    override var model: Int = 0,
    @Serializable(with = InventoryActionSerializer::class)
    override var action: CustomDynamicActions = CustomDynamicActions.NONE
) : IGUIActionItem