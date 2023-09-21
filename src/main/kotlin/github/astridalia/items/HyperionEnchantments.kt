package github.astridalia.items

import github.astridalia.inventory.toRoman
import org.bukkit.ChatColor
import java.util.*

enum class HyperionEnchantments(val maxLevel: Int, val startingLevel: Int) {
    EXPLODING_ARROW(5, 1),
    HEALING_SPELL(5, 1),
    LIFEDRAIN_SPELL(5, 1),
    GRID_PICKAXE(5, 1),
    PROJECTILE_MINING(5, 1),
    SPECIAL_BEAM(5, 1);

    val display: String
        get() = "${ChatColor.GRAY}${
            name.lowercase(Locale.getDefault()).replace("_", " ")
                .capitalizeWords()
        } ${ChatColor.AQUA}${startingLevel.toRoman()}"
}


fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }