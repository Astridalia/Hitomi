package github.astridalia.items.enchantments.v2

import github.astridalia.database.CachedMongoDBStorage
import github.astridalia.inventory.toRoman
import github.astridalia.items.PersistentData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType.INTEGER
import org.litote.kmongo.id.StringId

// TODO: Lore doesn't work properly still?
fun String.translateColors(): String = ChatColor.translateAlternateColorCodes('&', this)

@Serializable
data class CustomEnchant(
    var level: Int = 1,
    @BsonId @SerialName("_id")
    val name: String,
    val applicableMaterials: List<Material> = emptyList()
)

fun ItemStack.enchantOf(customEnchant: CustomEnchant): Int {
    val enchantment = getEnchantOf(customEnchant) ?: getOrDefault(customEnchant).level
    val dataContainer = itemMeta?.persistentDataContainer?.let { PersistentData<Int>(it, INTEGER) }
    dataContainer?.let { persistentData ->
        persistentData[customEnchant.name] = customEnchant.level
    }
    this.setItemMeta(itemMeta?.apply {
        addEnchant(Enchantment.DURABILITY, 1, true)
        addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        lore = (lore ?: mutableListOf()) + "${customEnchant.name} ${customEnchant.level.toRoman()}"
    })
    return enchantment
}

fun ItemStack.canEnchant(customEnchant: CustomEnchant): Boolean = customEnchant.applicableMaterials.contains(type)

fun ItemStack.getOrDefault(customEnchant: CustomEnchant): CustomEnchant {
    val cachedMongoDBStorage = CachedMongoDBStorage(CustomEnchant::class.java, "enchants")
    return cachedMongoDBStorage.get(StringId(customEnchant.name)) ?: run {
        cachedMongoDBStorage.insertOrUpdate(StringId(customEnchant.name), customEnchant)
        customEnchant
    }
}

fun ItemStack.getEnchantOf(customEnchant: CustomEnchant): Int? {
    val dataContainer = this.itemMeta?.persistentDataContainer
    val persistentData = dataContainer?.let { PersistentData<Int>(it, INTEGER) }
    return persistentData?.get(customEnchant.name)
}

fun ItemStack.removeEnchantOf(customEnchant: CustomEnchant) {
    val dataContainer = itemMeta?.persistentDataContainer?.let { PersistentData<Int>(it, INTEGER) }
    dataContainer?.let { persistentData ->
        persistentData.remove(customEnchant.name)
        itemMeta?.lore?.removeIf { it == "${customEnchant.name} ${customEnchant.level.toRoman()}" }
    }
    this.setItemMeta(itemMeta)
}