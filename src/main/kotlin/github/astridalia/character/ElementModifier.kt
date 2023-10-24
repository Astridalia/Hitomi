package github.astridalia.character
import kotlinx.serialization.Serializable


@Serializable
data class ElementModifier(
    val chaosElement: Double = 0.0,
    val earthElement: Double = 0.0,
    val equilibriumElement: Double = 0.0,
    val frostElement: Double = 0.0,
    val spiritElement: Double = 0.0,
    val tempestElement: Double = 0.0,
    val starAstral: Double = 0.0,
    val moonAstral: Double = 0.0,
    val sunAstral: Double = 0.0,
    val voidAstral: Double = 0.0
)