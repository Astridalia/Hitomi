package github.astridalia.items.rpg

import github.astridalia.items.SerializedItemStack

data class Equipment(
    val id: Int,
    val name: String,
    val description: String,
    val type: EquipmentType = EquipmentType.WEAPON,
    val rarity: EquipmentRarity = EquipmentRarity.COMMON,
    val slot: EquipmentSlot,
    val attributes: EquipmentAttributes,
    val itemStack: SerializedItemStack
)