package github.astridalia.items.enchantments

import github.astridalia.items.DynamicLore
import github.astridalia.items.enchantments.v2.CustomEnchant
import github.astridalia.items.enchantments.v2.enchantOf
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CustomEnchantments : KoinComponent {
    private val javaPlugin: JavaPlugin by inject()

    fun applyTo(itemStack: ItemStack, level: Int = 1, hyperionEnchantments: HyperionEnchantments) {
        val key = enchantmentKey(hyperionEnchantments)
        val itemMeta = itemStack.itemMeta ?: return
        modifyItem(itemMeta, hyperionEnchantments, level)
        applyProperties(itemMeta)
        setCustomEnchantmentLevel(itemMeta, key, level)
        itemStack.itemMeta = itemMeta
    }

    fun removeFrom(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments) {
        val itemMeta = itemStack.itemMeta ?: return
        val key = enchantmentKey(hyperionEnchantments)
        itemMeta.persistentDataContainer.remove(key)
        modifyItem(itemMeta, hyperionEnchantments)
        removeProperties(itemMeta)
        itemStack.itemMeta = itemMeta
    }


    // TODO: upgrading enchants, don't work right, and lore too?
    fun increaseEnchantmentLevelOrApply(
        itemStack: ItemStack,
        enchantmentBook: ItemStack,
        hyperionEnchantments: HyperionEnchantments
    ): Boolean {
//        val itemLevel = getFrom(itemStack, hyperionEnchantments)
//        val bookLevel = getFrom(enchantmentBook, hyperionEnchantments)
//        if (!canEnchant(itemStack, hyperionEnchantments, bookLevel)) return false
//        if (itemLevel == bookLevel)
//            applyTo(itemStack, itemLevel + 1, hyperionEnchantments) else applyTo(
//            itemStack,
//            hyperionEnchantments = hyperionEnchantments
//        )
        itemStack.enchantOf(CustomEnchant(name = "Fiery"))
        return true
    }

    fun getFrom(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments): Int =
        itemStack.itemMeta?.let { itemMeta ->
            val key = enchantmentKey(hyperionEnchantments)
            val container = itemMeta.persistentDataContainer
            container.getOrDefault(key, PersistentDataType.STRING, "0")
        }?.toInt() ?: 0

    private fun enchantmentKey(hyperionEnchantments: HyperionEnchantments): NamespacedKey =
        NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))

    private fun canEnchant(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments, bookLevel: Int): Boolean =
        hyperionEnchantments.isRightMaterial(itemStack.type) &&
                getFrom(itemStack, hyperionEnchantments) < hyperionEnchantments.maxLevel &&
                getFrom(itemStack, hyperionEnchantments) <= bookLevel

    private fun modifyItem(itemMeta: ItemMeta, hyperionEnchantments: HyperionEnchantments, level: Int = 1) {
        val displayString = hyperionEnchantments.displayName(level)
        val dynamicLore = DynamicLore()
        dynamicLore.addLineToSection("Enchantments", displayString)
        itemMeta.lore = dynamicLore.toLoreList()
    }

    private fun applyProperties(itemMeta: ItemMeta) {
        itemMeta.isUnbreakable = true
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
    }

    private fun setCustomEnchantmentLevel(itemMeta: ItemMeta, key: NamespacedKey, level: Int) {
        val container = itemMeta.persistentDataContainer
        container.set(key, PersistentDataType.STRING, level.toString())
    }

    private fun removeProperties(itemMeta: ItemMeta) {
        itemMeta.removeEnchant(Enchantment.DURABILITY)
    }
}