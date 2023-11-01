package github.astridalia.items.enchantments

import github.astridalia.HitomiPlugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

interface IEnchant {
    var name: String
    var description: String
    var level: Int
    var maxLevel: Int
    var applicableMaterials: MutableList<String>


    fun ItemStack.isApplicable(): Boolean {
        val material = type.name
        return applicableMaterials.contains(material)
    }

    private fun namespaceKey(str: String): NamespacedKey {
        val javaPlugin = JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java)
        return NamespacedKey(javaPlugin, str)
    }

    fun ItemStack.getEnchantOf(enchant: IEnchant): Int? {
        val persistentDataContainer = this.itemMeta?.persistentDataContainer
        return persistentDataContainer?.get(namespaceKey(enchant.name), PersistentDataType.INTEGER) ?: 0
    }

    fun ItemStack.removeEnchantOf(enchant: IEnchant) {
        val persistentDataContainer = this.itemMeta?.persistentDataContainer
        this.setItemMeta(this.itemMeta?.apply {
            persistentDataContainer?.remove(namespaceKey(enchant.name))
        })
    }

    fun ItemStack.setEnchantOf(enchant: IEnchant, level: Int) {
        val persistentDataContainer = this.itemMeta?.persistentDataContainer
        this.setItemMeta(this.itemMeta?.apply {
            persistentDataContainer?.set(namespaceKey(enchant.name), PersistentDataType.INTEGER, level)
        })
    }

    fun ItemStack.getOrDefault(enchant: IEnchant): IEnchant {
        val persistentDataContainer = this.itemMeta?.persistentDataContainer
        val level = persistentDataContainer?.get(namespaceKey(enchant.name), PersistentDataType.INTEGER) ?: 0
        this.setItemMeta(this.itemMeta?.apply {
            persistentDataContainer?.set(namespaceKey(enchant.name), PersistentDataType.INTEGER, level)
        })
        return enchant.apply { this.level = level }
    }

    fun toSerialized(): SerializableEnchant = SerializableEnchant(
        name = name,
        description = description,
        level = level,
        maxLevel = maxLevel,
        applicableMaterials = applicableMaterials
    )
}