package github.astridalia.items.enchantments

import github.astridalia.inventory.toRoman
import org.bukkit.ChatColor
import org.bukkit.Material

enum class HyperionEnchantments(val maxLevel: Int, val startingLevel: Int, val applicableMaterials: List<Material>) {
    EXPLODING_ARROW(10, 1, bows),
    HEALING_SPELL(5, 1, swords),
    LIFEDRAIN_SPELL(5, 1, swords),
    GRID_PICKAXE(10, 1, pickaxes),
    CLOAKING(10, 1, swords+ pickaxes),
    FIERY(10, 1, swords),
    CHARGED(10, 1, swords+ pickaxes),
    SOULBOUND(10, 1, emptyList()),
    PROJECTILE_MINING(10, 1, pickaxes);

    fun isRightMaterial(material: Material): Boolean {
        return applicableMaterials.isEmpty() || applicableMaterials.contains(material)
    }

    fun displayName(level: Int = 1): String {
        val enchantmentName = name.replace('_', ' ').lowercase().capitalizeWords()
        return "${ChatColor.GRAY}$enchantmentName ${ChatColor.AQUA}${level.toRoman()}"
    }

    companion object {
        private val enchantmentNameRegex = Regex("^hitomiplugin:", RegexOption.IGNORE_CASE)

        fun matches(name: String): HyperionEnchantments? {
            val formattedName = enchantmentNameRegex.replace(name, "")
            return entries.find { it.name.equals(formattedName, ignoreCase = true) }
        }

        fun isAtMaxLevel(level: Int, enchantment: HyperionEnchantments): Boolean {
            return level >= enchantment.maxLevel
        }
    }
}

val swords = listOf(
    Material.DIAMOND_SWORD,
    Material.GOLDEN_SWORD,
    Material.IRON_SWORD,
    Material.STONE_SWORD,
    Material.NETHERITE_SWORD,
    Material.WOODEN_SWORD
)

val bows = listOf(
    Material.BOW,
    Material.CROSSBOW
)

val pickaxes = listOf(
    Material.DIAMOND_PICKAXE,
    Material.GOLDEN_PICKAXE,
    Material.IRON_PICKAXE,
    Material.NETHERITE_PICKAXE,
    Material.STONE_PICKAXE,
    Material.WOODEN_PICKAXE,
)

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }



