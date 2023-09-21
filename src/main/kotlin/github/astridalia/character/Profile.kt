package github.astridalia.character

data class Profile(
    val _id: String,
    val stats: CharacterStats = CharacterStats(),
    val physicalAttributeModifier: PhysicalAttributeModifier = PhysicalAttributeModifier(),
    val astralRelation: AstralRelation = AstralRelation(),
    val elementalWeaknesses: ElementalWeaknesses = ElementalWeaknesses(),
    val elementModifier: ElementModifier = ElementModifier()
)