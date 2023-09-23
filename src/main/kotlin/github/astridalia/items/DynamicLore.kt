package github.astridalia.items

class DynamicLore {
    private val loreMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun addSection(section: String, lines: MutableList<String> = mutableListOf()) {
        loreMap[section] = lines
    }

    fun addLinesToSection(section: String, block: MutableList<String>.() -> Unit) {
        loreMap.getOrPut(section) { mutableListOf() }.apply(block)
    }

    fun findItemInSection(section: String, itemToFind: String): Int {
        val lines = loreMap[section] ?: return -1
        return lines.indexOf(itemToFind)
    }

    fun updateItemInSection(section: String, oldItem: String, newItem: String): Boolean {
        val lines = loreMap[section] ?: return false
        val index = findItemInSection(section, oldItem)

        if (index != -1) {
            lines[index] = newItem
            return true
        }

        return false
    }

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



