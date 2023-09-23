package github.astridalia.character

import github.astridalia.character.currency.CharacterCurrency

data class Profile(

    val _id: String,
    val stats: CharacterStats = CharacterStats(),
    val physicalAttributeModifier: PhysicalAttributeModifier = PhysicalAttributeModifier(),
    val astralRelation: AstralRelation = AstralRelation(),
    val elementalWeaknesses: ElementalWeaknesses = ElementalWeaknesses(),
    val elementModifier: ElementModifier = ElementModifier(),
    val characterCurrency: CharacterCurrency = CharacterCurrency(_id, 0)
)