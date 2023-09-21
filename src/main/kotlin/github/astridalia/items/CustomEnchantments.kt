package github.astridalia.items

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CustomEnchantments : KoinComponent {

    private val javaPlugin: JavaPlugin by inject()

    fun applyTo(
        itemStack: ItemStack,
        level: Int,
        hyperionEnchantments: HyperionEnchantments,
        dynamicLore: DynamicLore = DynamicLore()
    ) {
        val key = NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))
        val itemMeta = itemStack.itemMeta ?: return

        modifyLore(itemMeta, hyperionEnchantments, level, dynamicLore)
        applyOtherProperties(itemMeta)
        setCustomEnchantmentLevel(itemMeta, key, level)

        itemStack.itemMeta = itemMeta
    }

    fun removeFrom(itemStack: ItemStack, hyperionEnchantments: HyperionEnchantments, dynamicLore: DynamicLore) {
        val itemMeta = itemStack.itemMeta ?: return
        val key = NamespacedKey(javaPlugin, hyperionEnchantments.name.replace(" ", "_"))
        val container = itemMeta.persistentDataContainer
        container.remove(key)
        modifyLore(itemMeta, hyperionEnchantments, 0, dynamicLore)
        removeOtherProperties(itemMeta)
        itemStack.itemMeta = itemMeta
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
        level: Int = 1,
        dynamicLore: DynamicLore
    ) {
        val displayString = hyperionEnchantments.display
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