package github.astridalia.character.inventories

import kotlinx.serialization.Serializable
import org.bukkit.inventory.Inventory

@Serializable
data class CharacterInventory(
    val _id: String,
    val inventory: Inventory
)
