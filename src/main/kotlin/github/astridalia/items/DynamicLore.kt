package github.astridalia.items

import kotlinx.serialization.Serializable



@Serializable
object DynamicLore{
    private var loreMap: MutableMap<String, MutableList<String>> = mutableMapOf()


    fun addLineToSection(section: String, line: String) {
        loreMap.getOrPut(section) { mutableListOf() } += line
    }

    fun removeLineFromSection(section: String, line: String) {
        loreMap[section]?.remove(line)
    }

    fun toLoreList(): List<String> {
        return loreMap.flatMap { (section, lines) ->
            if (lines.isNotEmpty()) {
                val sectionHeader = "[$section]"
                val sectionLines = lines.map { "- $it" }
                listOf(sectionHeader) + sectionLines
            } else {
                emptyList()
            }
        }
    }

    override fun toString(): String {
        return "DynamicLore(loreMap=$loreMap)"
    }
}



