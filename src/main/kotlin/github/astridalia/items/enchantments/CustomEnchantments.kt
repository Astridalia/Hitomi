package github.astridalia.items.enchantments

import github.astridalia.items.DynamicLore
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

    fun applyTo(
        itemStack: ItemStack,
        level: Int = 1,
        hyperionEnchantments: HyperionEnchantments,
        dynamicLore: DynamicLore = DynamicLore()
    ) {
        val key = NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))
        val itemMeta = itemStack.itemMeta ?: return
        modifyLore(itemMeta, hyperionEnchantments, dynamicLore, level)
        applyOtherProperties(itemMeta)
        setCustomEnchantmentLevel(itemMeta, key, level)
        itemStack.itemMeta = itemMeta
    }

    fun removeFrom(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments, dynamicLore: DynamicLore) {
        val itemMeta = itemStack.itemMeta ?: return
        val key = NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))
        val container = itemMeta.persistentDataContainer
        container.remove(key)
        modifyLore(itemMeta, hyperionEnchantments, dynamicLore)
        removeOtherProperties(itemMeta)
        itemStack.itemMeta = itemMeta
    }

    fun increaseEnchantmentLevelOrApply(
        itemStack: ItemStack,
        enchantmentBook: ItemStack,
        hyperionEnchantments: HyperionEnchantments
    ): Boolean {
        val itemToEnchant = getFrom(itemStack, hyperionEnchantments)
        val enchantmentBookLevel = getFrom(enchantmentBook, hyperionEnchantments)
        when {
            !hyperionEnchantments.isRightMaterial(hyperionEnchantments, itemStack.type) -> return false
            itemToEnchant >= hyperionEnchantments.maxLevel -> return false
            itemToEnchant <= 0 -> applyTo(itemStack, enchantmentBookLevel, hyperionEnchantments)
            itemToEnchant == enchantmentBookLevel -> {
                removeFrom(itemStack, hyperionEnchantments, DynamicLore())
                applyTo(itemStack, itemToEnchant + 1, hyperionEnchantments, DynamicLore())
            }

            itemToEnchant < enchantmentBookLevel -> return false
        }
        return true
    }

    fun getFrom(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments): Int {
        val itemMeta = itemStack.itemMeta ?: return 0
        val key = NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))
        val container = itemMeta.persistentDataContainer
        return container.getOrDefault(key, PersistentDataType.INTEGER, 0)
    }

    private fun modifyLore(
        itemMeta: ItemMeta,
        hyperionEnchantments: HyperionEnchantments,
        dynamicLore: DynamicLore,
        level: Int = 1
    ) {
        val displayString = hyperionEnchantments.displayLevel(hyperionEnchantments, level)
        dynamicLore.addLineToSection("Enchantments", displayString)
        itemMeta.lore = dynamicLore.toLoreList()
    }

    private fun applyOtherProperties(itemMeta: ItemMeta) {
        itemMeta.isUnbreakable = true
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
    }

    private fun setCustomEnchantmentLevel(itemMeta: ItemMeta, key: NamespacedKey, level: Int) {
        val container = itemMeta.persistentDataContainer
        container.set(key, PersistentDataType.INTEGER, level)
    }

    private fun removeOtherProperties(itemMeta: ItemMeta) {
        itemMeta.removeEnchant(Enchantment.DURABILITY)
    }
}