package github.astridalia.items.enchantments

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
import java.util.*

// TODO: Lore doesn't work properly still?
fun String.translateColors(): String = ChatColor.translateAlternateColorCodes('&', this)

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
            val cachedMongoDBStorage = CachedMongoDBStorage(CustomEnchant::class.java, "enchants")
            return cachedMongoDBStorage.get(StringId(formattedName))
        }
    }
}

fun ItemStack.enchantOf(customEnchant: CustomEnchant): Int {
    val itemMeta = itemMeta ?: return 0
    val dataContainer = itemMeta.persistentDataContainer
    val persistentData = PersistentData(dataContainer, INTEGER)
    val enchantName = customEnchant.name
    val loreToAdd = "${ChatColor.DARK_PURPLE}$enchantName ${ChatColor.AQUA}${customEnchant.level.toRoman()}"
    val existingLevel = persistentData[enchantName]

    if (existingLevel == null || existingLevel < customEnchant.level) {
        with(itemMeta) {
            lore?.removeIf { it.startsWith("${ChatColor.DARK_PURPLE}$enchantName ") }
            addEnchant(Enchantment.DURABILITY, 1, true)
            addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
            lore = (lore ?: mutableListOf()) + loreToAdd
        }
        persistentData[enchantName] = customEnchant.level
        this.itemMeta = itemMeta
    } else if (existingLevel == customEnchant.level) {
        // Increment level when equal
        with(itemMeta) {
            persistentData[enchantName] = customEnchant.level + 1
            lore = lore?.map {
                if (it.startsWith("${ChatColor.DARK_PURPLE}$enchantName "))
                    "${ChatColor.DARK_PURPLE}$enchantName ${ChatColor.AQUA}${(customEnchant.level + 1).toRoman()}" else it
            }?.toMutableList() ?: mutableListOf()
        }
        this.itemMeta = itemMeta
    }

    return getEnchantOf(customEnchant) ?: getOrDefault(customEnchant).level
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

        val loreToRemove = "${customEnchant.name} ${customEnchant.level.toRoman()}"

        itemMeta?.lore?.remove(loreToRemove)
        println("Removed lore: $loreToRemove")
    }
    this.setItemMeta(itemMeta)
}