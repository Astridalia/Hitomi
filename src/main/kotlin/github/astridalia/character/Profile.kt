package github.astridalia.character

import github.astridalia.character.currency.CharacterCurrency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId


@Serializable
data class Profile(
    @BsonId @SerialName("_id") val _id: String,
    val stats: CharacterStats = CharacterStats(),
    val physicalAttributeModifier: PhysicalAttributeModifier = PhysicalAttributeModifier(),
    val astralRelation: AstralRelation = AstralRelation(),
    val elementalWeaknesses: ElementalWeaknesses = ElementalWeaknesses(),
    val elementModifier: ElementModifier = ElementModifier(),
    val characterCurrency: CharacterCurrency = CharacterCurrency(_id, 0)
)