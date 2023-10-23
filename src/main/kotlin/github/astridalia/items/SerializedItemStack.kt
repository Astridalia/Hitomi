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
    var _id: String = UUID.randomUUID().toString(),
    var type: String = "STONE",
    var name: String = "test_item",
    var lore: MutableList<String> = mutableListOf(),
    var model: Int = 0,
    var persistentData: MutableMap<String, String> = hashMapOf("timestamp" to System.currentTimeMillis().toString()),
    var dynamicLore: DynamicLore = DynamicLore
) : KoinComponent {
    private fun namespaceKey(str: String): NamespacedKey {
        val javaPlugin: JavaPlugin by inject()
        return NamespacedKey(javaPlugin, str)
    }

    fun toItemStack(amount: Int = 1): ItemStack {
        val material = Material.matchMaterial(type) ?: Material.STONE
        val itemStack = ItemStack(material, amount)
        itemStack.itemMeta?.apply {
            setDisplayName(name)
            lore = this.lore
            setCustomModelData(model)
            isUnbreakable = true
            persistentData.forEach { (k, v) ->
                val namespaceKey = namespaceKey(k)
                persistentDataContainer.set(namespaceKey, PersistentDataType.STRING, v)
            }
        }
        return itemStack
    }
}

fun ItemStack.toSerialized(): SerializedItemStack {
    val persistentData = mutableMapOf<String, String>()
    val persistentDataContainer = itemMeta?.persistentDataContainer
    val filteredKeys =
        persistentDataContainer?.keys?.filter { it.key == HitomiPlugin::class.simpleName!!.lowercase(Locale.getDefault()) }
    val filteredData =
        filteredKeys?.associate { it.key to persistentData[it.key].toString() }?.toMutableMap() ?: persistentData
    return SerializedItemStack(
        type = type.name,
        name = itemMeta?.displayName ?: "test_item",
        lore = itemMeta?.lore?.toMutableList() ?: mutableListOf(),
        model = itemMeta?.customModelData ?: 0,
        persistentData = filteredData
    )
}
