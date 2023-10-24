package github.astridalia.character

import github.astridalia.events.CustomAttributeType
import kotlinx.serialization.Serializable


@Serializable
data class ElementalWeaknesses(
    val chaosWeakness: Double = 0.0,
    val earthWeakness: Double = 0.0,
    val equilibriumWeakness: Double = 0.0,
    val frostWeakness: Double = 0.0,
    val spiritWeakness: Double = 0.0,
    val tempestWeakness: Double = 0.0,
    val combustionWeakness: Double = 0.0
) {
    fun getElementalWeakness(element: CustomAttributeType): Double {
        return when (element) {
            CustomAttributeType.EarthElement -> earthWeakness
            CustomAttributeType.ChaosElement -> chaosWeakness
            CustomAttributeType.FrostElement -> frostWeakness
            CustomAttributeType.SpiritElement -> spiritWeakness
            CustomAttributeType.TempestElement -> tempestWeakness
            CustomAttributeType.CombustionElement -> combustionWeakness
        }
    }
}