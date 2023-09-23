package github.astridalia.modules.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.util.Vector

@Serializable
@SerialName("Vector")
data class VectorSurrogate(
    val x: Double,
    val y: Double,
    val z: Double
) {
    constructor(vector: Vector) : this(vector.x, vector.y, vector.z)
}
