package github.astridalia.character

import kotlinx.serialization.Serializable

@Serializable
data class PhysicalAttributeModifier(
    val physical: Double = 1.5,
    val stamina: Double = 100.0,
    val mana: Double = 100.0,
    val resists: Double = 0.0,
    val dodging: Double = 0.05
)
