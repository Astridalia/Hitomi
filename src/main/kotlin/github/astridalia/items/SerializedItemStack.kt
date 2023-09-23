package github.astridalia.items

import github.astridalia.HitomiPlugin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
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
) {


    fun addData(name: String, value: String) {
        persistentData.putIfAbsent(name, value)
    }

    fun removeData(name: String) {
        persistentData.remove(name)
    }


    fun toItemStack(amount: Int = 1): ItemStack {
        val material = Material.matchMaterial(type) ?: Material.STONE
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta?.apply {
            setDisplayName(name)
            lore = this@SerializedItemStack.lore
            setCustomModelData(model)
            persistentData.forEach { (k, v) ->
                val namespaceKey = NamespacedKey(
                    HitomiPlugin::class.simpleName!!.lowercase(Locale.getDefault()),
                    k
                )
                persistentDataContainer.set(namespaceKey, PersistentDataType.STRING, v)
            }
            isUnbreakable = true
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

