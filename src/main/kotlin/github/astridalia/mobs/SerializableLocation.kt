package github.astridalia.mobs

import kotlinx.serialization.Serializable

@Serializable
data class SerializableLocation(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double
)