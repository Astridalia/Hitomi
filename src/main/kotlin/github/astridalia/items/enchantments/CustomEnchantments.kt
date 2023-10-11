package github.astridalia.items.enchantments

import github.astridalia.database.RedisCache
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
import java.util.*

@Serializable
data class CustomEnchant(
    var level: Int = 1,
    @BsonId @SerialName("_id")
    val name: String,
    val applicableMaterials: List<Material> = emptyList()
) {
    companion object {
        private val enchantmentNameRegex = Regex("^hitomiplugin:", RegexOption.IGNORE_CASE)

        fun matches(name: String): CustomEnchant? {
            val formattedName = enchantmentNameRegex.replace(name.lowercase(Locale.getDefault()), "")
            val cachedMongoDBStorage = RedisCache(CustomEnchant::class.java, "enchants")
            return cachedMongoDBStorage.get(StringId(formattedName))
        }
    }
}

fun ItemStack.enchantOf(customEnchant: CustomEnchant): Int {
    val itemMeta = itemMeta ?: return 0
    val dataContainer = itemMeta.persistentDataContainer
    val persistentData = PersistentData(dataContainer, INTEGER)
    val enchantName = customEnchant.name
    val existingLevel = persistentData[enchantName] ?: 0
    val loreToAdd = "${ChatColor.DARK_PURPLE}$enchantName ${ChatColor.AQUA}${existingLevel.toRoman()}"
    if (existingLevel <= 0) {
        with(itemMeta) {
            lore?.removeIf { it.startsWith("${ChatColor.DARK_PURPLE}$enchantName ") }
            addEnchant(Enchantment.DURABILITY, 1, true)
            addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            lore = (lore ?: mutableListOf()) + loreToAdd
        }
        persistentData[enchantName] = customEnchant.level
        this.itemMeta = itemMeta

    } else if (existingLevel >= customEnchant.level) {
        with(itemMeta) {
            val current = existingLevel + customEnchant.level
            persistentData[enchantName] = current
            lore = lore?.map {
                if (it.startsWith("${ChatColor.DARK_PURPLE}$enchantName "))
                    "${ChatColor.DARK_PURPLE}$enchantName ${ChatColor.AQUA}${current.toRoman()}" else it
            }?.toMutableList() ?: mutableListOf()
        }
        this.itemMeta = itemMeta
    }
    return getEnchantOf(customEnchant) ?: getOrDefault(customEnchant).level
}

fun ItemStack.canEnchant(customEnchant: CustomEnchant): Boolean = customEnchant.applicableMaterials.contains(type)

fun ItemStack.getOrDefault(customEnchant: CustomEnchant): CustomEnchant {
    val cachedMongoDBStorage = RedisCache(CustomEnchant::class.java, "enchants")
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

        val loreToRemove = "${customEnchant.name} ${customEnchant.level.toRoman()}"

        itemMeta?.lore?.remove(loreToRemove)
        println("Removed lore: $loreToRemove")
    }
    this.setItemMeta(itemMeta)
}