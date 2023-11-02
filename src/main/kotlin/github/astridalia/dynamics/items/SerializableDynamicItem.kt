package github.astridalia.dynamics.items

import kotlinx.serialization.Serializable


@Serializable
data class SerializableDynamicItem(
    override var type: String,
    override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(
        "DateGeneratedBy" to "${System.currentTimeMillis()}"
    ),
    override var model: Int = 0
) : IItemComponent
