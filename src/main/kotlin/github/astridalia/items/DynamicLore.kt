package github.astridalia.items

class DynamicLore {
    private val loreMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun addSection(section: String, lines: MutableList<String> = mutableListOf()) {
        loreMap[section] = lines
    }

    fun addLinesToSection(section: String, block: MutableList<String>.() -> Unit) {
        loreMap.getOrPut(section) { mutableListOf() }.apply(block)
    }

    fun addLineToSection(section: String, line: String) {
        loreMap.getOrPut(section) { mutableListOf() }.add(line)
    }

    fun removeLineFromSection(section: String, line: String) {
        loreMap[section]?.remove(line)
    }

    fun toLoreList(): List<String> {
        return loreMap.flatMap { (section, lines) ->
            if (lines.isNotEmpty() && section != "ignoreSection") listOf("[$section]") + lines + listOf("") else {
                emptyList()
            }
        }
    }
}

