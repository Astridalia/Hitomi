package github.astridalia.dynamics.items

import github.astridalia.HitomiPlugin
import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.enchantOf
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.getEnchantOf
import github.astridalia.dynamics.items.enchantments.SerializableEnchant.Companion.setEnchantOf
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

interface IItemComponent {
    var type: Material
    var name: String
    var lore: MutableList<String>
    var data: MutableMap<String, String>
    var model: Int

    fun namespaceKey(str: String): NamespacedKey {
        val javaPlugin = JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java)
        return NamespacedKey(javaPlugin, str)
    }

    fun addData(key: String, value: String) {
        data[key] = value
    }

    fun getData(key: String): String? {
        return data[key]
    }

    fun removeData(key: String) {
        data.remove(key)
    }



    fun toItemStack(amount: Int = 1): ItemStack {
        val itemStack = ItemStack(type, amount)
        itemStack.setItemMeta(itemStack.itemMeta?.apply {
            setDisplayName(name)
            lore = this@IItemComponent.lore
            setCustomModelData(model)
            isUnbreakable = true
            data.forEach { (k, v) ->
                val namespaceKey = namespaceKey(k)
                if (v.toIntOrNull() != null) {
                    // If 'v' is an integer, set it as an integer
                    persistentDataContainer.set(namespaceKey, PersistentDataType.INTEGER, v.toInt())
                } else {
                    val enchantment = SerializableEnchant.matches(k)
                    if (enchantment != null) {
                        // If 'v' is not an integer, treat it as an enchantment level (default to 1 if not a valid integer)
                        val level = v.toIntOrNull() ?: 1
                        itemStack.setEnchantOf(enchantment, level)
                    }
                }
            }
        })
        return itemStack
    }

    fun toSerialized(): SerializableDynamicItem = SerializableDynamicItem(
        type = type,
        name = name,
        lore = lore,
        data = data,
        model = model
    )
}