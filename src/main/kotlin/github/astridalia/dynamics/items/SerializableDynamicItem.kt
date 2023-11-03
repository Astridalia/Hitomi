package github.astridalia.dynamics.items

import github.astridalia.modules.serializers.MaterialSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.Material


@Serializable
data class SerializableDynamicItem(
    @Serializable(with = MaterialSerializer::class)
    override var type: Material,
    @BsonId @SerialName("_id") override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(
        "DateGeneratedBy" to "${System.currentTimeMillis()}"
    ),
    override var model: Int = 0
) : IItemComponent
