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
    @BsonId @SerialName("_id")
    var _id: String = "fiery",
    var applicableMaterials: MutableList<Material> = mutableListOf(),
    var level: Int = 1,
    var maxLevel: Int = 5
) {

    companion object {
        private val enchantmentNameRegex = Regex("^hitomiplugin:", RegexOption.IGNORE_CASE)

        fun matches(name: String): CustomEnchant? {
            val formattedName = enchantmentNameRegex.replace(name.lowercase(Locale.getDefault()), "")
            return RedisCache(CustomEnchant::class.java).get(StringId(formattedName))
        }
    }
}

fun ItemStack.enchantOf(customEnchant: CustomEnchant): Int {
    val itemMeta = itemMeta ?: return 0
    val dataContainer = itemMeta.persistentDataContainer
    val persistentData = PersistentData(dataContainer, INTEGER)
    val data = persistentData[customEnchant._id] ?: 0

    itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

    val newLevel = data + customEnchant.level
    persistentData[customEnchant._id] = newLevel
    val loreToAdd = "${ChatColor.DARK_AQUA}${customEnchant._id} ${ChatColor.BLUE}${newLevel.toRoman()}"
    val loreToRemove = "${ChatColor.DARK_AQUA}${customEnchant._id} ${ChatColor.BLUE}${data.toRoman()}"
    itemMeta.lore = (itemMeta.lore ?: mutableListOf()) - loreToRemove + loreToAdd

    this.setItemMeta(itemMeta)
    return getEnchantOf(customEnchant) ?: getOrDefault(customEnchant).level
}

fun ItemStack.canEnchant(customEnchant: CustomEnchant): Boolean {
    val contains = customEnchant.applicableMaterials.contains(type)
    return !(!contains && customEnchant.level == customEnchant.maxLevel)
}

fun ItemStack.getOrDefault(customEnchant: CustomEnchant): CustomEnchant {
    return RedisCache(CustomEnchant::class.java).get(StringId(customEnchant._id)) ?: run {
        RedisCache(CustomEnchant::class.java).insertOrUpdate(StringId(customEnchant._id), customEnchant)
        customEnchant
    }
}

fun ItemStack.getEnchantOf(customEnchant: CustomEnchant): Int? {
    val dataContainer = this.itemMeta?.persistentDataContainer
    val persistentData = dataContainer?.let { PersistentData<Int>(it, INTEGER) }
    return persistentData?.get(customEnchant._id)
}