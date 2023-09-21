package github.astridalia.mobs

import kotlinx.serialization.Serializable

@Serializable
data class Entity(
    val id: Int,
    val type: String,
    val name: String,
    val age: Int = 5,
)