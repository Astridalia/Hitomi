package github.astridalia.mobs

import kotlinx.serialization.Serializable

@Serializable
data class SerializableEntity(
    val _id: Int,
    val behaviorTypes: EntityBehaviorTypes = EntityBehaviorTypes.FRIENDLY,
    val type: String,
    val name: String,
    val age: Int = 5,
    val location: SerializableLocation
)