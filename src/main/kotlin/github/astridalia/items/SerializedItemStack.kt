package github.astridalia.items

import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


@Serializable
data class SerializedItemStack(
    val type: String,
    val amount: Int = 1,
    val durability: Short,
    val displayName: String?,
    val lore: List<String>?,
    val modelId: Int = 0
) {
    fun createItemStack(): ItemStack {
        val itemStack = ItemStack(Material.valueOf(type), amount)
        val itemMeta = itemStack.itemMeta!!
        itemMeta.setDisplayName(displayName)
        itemMeta.lore = lore
        itemMeta.setCustomModelData(modelId)
        itemStack.setItemMeta(itemMeta)
        return itemStack
    }
}