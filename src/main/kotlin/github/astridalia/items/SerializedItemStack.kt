package github.astridalia.items

import github.astridalia.HitomiPlugin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


@Serializable
data class SerializedItemStack(
    @SerialName("_id")
    val _id: String = UUID.randomUUID().toString(),
    val type: String = "STONE",
    val name: String = "test_item",
    val lore: MutableList<String> = mutableListOf(),
    val model: Int = 0,
    val persistentData: MutableMap<String, String> = hashMapOf("timestamp" to System.currentTimeMillis().toString()),
    val dynamicLore: DynamicLore = DynamicLore()
) : KoinComponent {
    private fun namespaceKey(str: String): NamespacedKey {
        val javaPlugin: JavaPlugin by inject()
        return NamespacedKey(javaPlugin, str)
    }

    fun toItemStack(amount: Int = 1): ItemStack {
        val material = Material.matchMaterial(type) ?: Material.STONE
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta ?: return itemStack
        itemMeta.setDisplayName(name)
        itemMeta.lore = lore
        itemMeta.setCustomModelData(model)
        itemMeta.isUnbreakable = true
        persistentData.forEach { (k, v) ->
            val namespaceKey = namespaceKey(k)
            itemMeta.persistentDataContainer.set(namespaceKey, PersistentDataType.STRING, v)
        }
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}

fun ItemStack.toSerialized(): SerializedItemStack {
    val persistentData = mutableMapOf<String, String>()
    return SerializedItemStack(
        type = this.type.name,
        name = this.itemMeta?.displayName ?: "test_item",
        lore = this.itemMeta?.lore ?: mutableListOf(),
        model = this.itemMeta?.customModelData ?: 0,
        persistentData = this.itemMeta?.persistentDataContainer
            ?.keys
            ?.filter { it.key == HitomiPlugin::class.simpleName!!.lowercase(Locale.getDefault()) }
            ?.associate { it.key to persistentData[it.key].toString() }
            ?.toMutableMap()
            ?: persistentData
    )
}

