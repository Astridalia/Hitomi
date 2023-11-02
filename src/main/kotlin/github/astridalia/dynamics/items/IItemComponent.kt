package github.astridalia.dynamics.items

import github.astridalia.HitomiPlugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
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
                persistentDataContainer.set(namespaceKey, PersistentDataType.STRING, v)
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