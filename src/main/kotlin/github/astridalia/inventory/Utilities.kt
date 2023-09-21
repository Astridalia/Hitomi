package github.astridalia.inventory

import github.astridalia.items.SerializedItemStack
import github.astridalia.modules.serializers.ItemStackSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun ItemStackSerializer.createItemStack(serializedItemStack: SerializedItemStack): ItemStack {
    val itemStack = ItemStack(Material.valueOf(serializedItemStack.type), serializedItemStack.amount)
    val itemMeta = itemStack.itemMeta!!
    itemMeta.setDisplayName(serializedItemStack.displayName)
    itemMeta.lore = serializedItemStack.lore
    itemMeta.setCustomModelData(serializedItemStack.modelId)

    itemStack.setItemMeta(itemMeta)


    return itemStack
}

fun Int.toRoman(): String {
    val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    var num = this
    val result = StringBuilder()

    for (i in values.indices) {
        while (num >= values[i]) {
            num -= values[i]
            result.append(symbols[i])
        }
    }

    return result.toString()
}