package github.astridalia.character

import kotlinx.serialization.Serializable


@Serializable
data class AstralRelation(
    val starRelation: Double = 0.0,
    val moonRelation: Double = 0.0,
    val sunRelation: Double = 0.0,
    val voidRelation: Double = 0.0
)