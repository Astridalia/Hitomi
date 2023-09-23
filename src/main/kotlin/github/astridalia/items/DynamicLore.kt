package github.astridalia.items

import kotlinx.serialization.Serializable


// TODO: Revamp this entire class, because its inaccurate and doesn't work properly.
// NOTE: We should add in more functionality for shifting down on an item to show more info!

@Serializable
data class DynamicLore(
    val loreMap: MutableMap<String, MutableList<String>> = mutableMapOf()
) {

    fun addLineToSection(section: String, line: String) {
        loreMap.getOrPut(section) { mutableListOf() } += line
    }

    fun removeLineFromSection(section: String, line: String) {
        loreMap[section]?.remove(line)
    }

    fun toLoreList(): List<String> {
        return loreMap.flatMap { (section, lines) ->
            if (lines.isNotEmpty() && section != "ignoreSection") {
                listOf("[$section]") + lines
            } else {
                emptyList()
            }
        }
    }
}




