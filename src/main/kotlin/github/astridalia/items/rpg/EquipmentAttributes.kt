package github.astridalia.items.rpg

import github.astridalia.character.*

data class EquipmentAttributes(
    val stats: CharacterStats = CharacterStats(),
    val physicalAttributeModifier: PhysicalAttributeModifier = PhysicalAttributeModifier(),
    val astralRelation: AstralRelation = AstralRelation(),
    val elementalWeaknesses: ElementalWeaknesses = ElementalWeaknesses(),
    val elementModifier: ElementModifier = ElementModifier()
)