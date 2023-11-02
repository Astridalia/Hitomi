package github.astridalia.dynamics.items.enchantments

import github.astridalia.HitomiPlugin
import github.astridalia.database.RedisCache
import github.astridalia.inventory.toRoman
import github.astridalia.items.PersistentData
import kotlinx.serialization.Serializable
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.id.StringId
import java.util.*


@Serializable
data class SerializableEnchant(
    override var name: String,
    override var description: String = "",
    override var level: Int,
    override var maxLevel: Int = 5,
    override var applicableMaterials: MutableList<String> = mutableListOf(),
    override var display: String = String.format(
        "&5%s &a%s",
        name.uppercase(Locale.getDefault()).replace("_", " "),
        level.toRoman()
    ).toColor()
) : IEnchant {


    fun ItemStack.isApplicable(): Boolean = applicableMaterials.contains(this.type.name)

    companion object {
        private val enchantmentNameRegex = Regex("^hitomiplugin:", RegexOption.IGNORE_CASE)
        fun String.toColor(): String = ChatColor.translateAlternateColorCodes('&', this)
        fun matches(name: String): SerializableEnchant? {
            val formattedName = enchantmentNameRegex.replace(name.lowercase(Locale.getDefault()), "")
            return RedisCache(SerializableEnchant::class.java).get(
                StringId(
                    formattedName.replace(
                        "\\s+".toRegex(),
                        "_"
                    )
                )
            )
        }

        fun createLoreString(enchantName: String, level: Int): String {
            val humanReadableName = enchantName.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            } }
            return String.format("%s%s %s%s", ChatColor.DARK_AQUA, humanReadableName, ChatColor.BLUE, level.toRoman())
        }

        fun ItemStack.enchantOf(customEnchant: SerializableEnchant): Int {
            val itemMeta = itemMeta ?: return 0
            val dataContainer = itemMeta.persistentDataContainer
            val persistentData = PersistentData(dataContainer, PersistentDataType.INTEGER)
            val data = persistentData[customEnchant.name] ?: 0

            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

            val newLevel = data + customEnchant.level
            persistentData[customEnchant.name] = newLevel
            val loreToAdd = createLoreString(customEnchant.name, newLevel)
            val loreToRemove = createLoreString(customEnchant.name, data)
            itemMeta.lore = (itemMeta.lore ?: mutableListOf()) - loreToRemove + loreToAdd

            this.setItemMeta(itemMeta)
            return getEnchantOf(customEnchant) ?: getOrDefault(customEnchant)
        }

        private fun namespaceKey(str: String): NamespacedKey {
            val javaPlugin = JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java)
            return NamespacedKey(javaPlugin, str)
        }

        fun ItemStack.getEnchantOf(enchant: IEnchant): Int {
            val persistentDataContainer = this.itemMeta?.persistentDataContainer
            return persistentDataContainer?.get(namespaceKey(enchant.name), PersistentDataType.INTEGER) ?: 0
        }

        fun ItemStack.removeEnchantOf(enchant: IEnchant) {
            val persistentDataContainer = this.itemMeta?.persistentDataContainer
            this.setItemMeta(this.itemMeta?.apply {
                this.removeEnchant(Enchantment.DURABILITY)
                persistentDataContainer?.remove(namespaceKey(enchant.name))
            })
        }

        fun ItemStack.setEnchantOf(enchant: IEnchant, level: Int) {
            val persistentDataContainer = this.itemMeta?.persistentDataContainer
            this.setItemMeta(this.itemMeta?.apply {
                this.addEnchant(Enchantment.DURABILITY, 1, true)
                this.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                persistentDataContainer?.set(namespaceKey(enchant.name), PersistentDataType.INTEGER, level)
            })
        }

        fun ItemStack.getOrDefault(enchant: IEnchant): Int {
            val persistentDataContainer = this.itemMeta?.persistentDataContainer
            val level = persistentDataContainer?.get(namespaceKey(enchant.name), PersistentDataType.INTEGER) ?: 0
            this.setItemMeta(this.itemMeta?.apply {
                persistentDataContainer?.set(namespaceKey(enchant.name), PersistentDataType.INTEGER, level)
            })
            return enchant.level
        }
    }
}
