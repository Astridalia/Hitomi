package github.astridalia.items.enchantments

import github.astridalia.inventory.toRoman
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.*

enum class HyperionEnchantments(val maxLevel: Int, val startingLevel: Int, val applicableMaterials: List<Material>) {
    EXPLODING_ARROW(5, 1, listOf(Material.BOW, Material.CROSSBOW)),
    HEALING_SPELL(5, 1, emptyList()),
    LIFEDRAIN_SPELL(5, 1, emptyList()),
    GRID_PICKAXE(5, 1, pickaxes),
    PROJECTILE_MINING(5, 1, pickaxes);

    fun isRightMaterial(hyperionEnchantments: HyperionEnchantments, material: Material): Boolean =
        hyperionEnchantments.applicableMaterials.contains(material)

    fun displayLevel(hyperionEnchantments: HyperionEnchantments, level: Int = 1): String {
        return "${ChatColor.GRAY}${
            hyperionEnchantments.name.replace('_', ' ').lowercase(Locale.getDefault())
                .capitalizeWords()
        } ${ChatColor.AQUA}${level.toRoman()}"
    }

    companion object {


        fun matches(name: String): HyperionEnchantments? {
            val named = name.replace("hitomiplugin:", "")
            return entries.find { it.name.equals(named, ignoreCase = true) }
        }

        fun isAtMaxLevel(level: Int, hyperionEnchantments: HyperionEnchantments): Boolean {
            return level >= hyperionEnchantments.maxLevel
        }
    }
}

val pickaxes = listOf(
    Material.DIAMOND_PICKAXE,
    Material.GOLDEN_PICKAXE,
    Material.IRON_PICKAXE,
    Material.NETHERITE_PICKAXE,
    Material.STONE_PICKAXE,
    Material.WOODEN_PICKAXE
)

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

