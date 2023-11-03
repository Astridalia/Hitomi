package github.astridalia.dynamics.items

import github.astridalia.dynamics.DynamicStats
import github.astridalia.modules.serializers.MaterialSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.Material


@Serializable
data class SerializableDynamicStatItem(
    override var strength: Int = 0,
    override var intellect: Int = 0,
    override var agility: Int = 0,
    override var will: Int = 0,
    override var power: Int = 0,
    @Serializable(with = MaterialSerializer::class)
    override var type: Material,
    @BsonId @SerialName("_id") override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(
        "DateGeneratedBy" to "${System.currentTimeMillis()}",
        "power" to "$power",
        "will" to "$will",
        "agility" to "$agility",
        "intellect" to "$intellect",
        "strength" to "$strength",
    ),
    override var model: Int = 0,
    override var dynamicStatItemMutableSet: MutableSet<SerializableDynamicStatItem> = mutableSetOf()
) : IItemComponent, DynamicStats