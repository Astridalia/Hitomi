package github.astridalia.items

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PersistentData<T : Any>(
    private val holder: PersistentDataContainer,
    private val persistentDataType: PersistentDataType<out T, T>,
    override val size: Int = Int.MAX_VALUE,
    override val entries: MutableSet<MutableMap.MutableEntry<String, T>> = mutableSetOf(),
    override val keys: MutableSet<String> = mutableSetOf(),
    override val values: MutableCollection<T> = mutableSetOf()
) : KoinComponent, MutableMap<String, T> {


    private fun namespaceKey(key: String): NamespacedKey {
        val javaPlugin: JavaPlugin by inject()
        return NamespacedKey(javaPlugin, key)
    }

    override fun containsKey(key: String): Boolean = holder.keys.contains(namespaceKey(key))

    override fun containsValue(value: T): Boolean = entries.any { it.value == value }

    override fun get(key: String): T? = holder[namespaceKey(key), persistentDataType]

    override fun clear() {
        holder.keys.clear()
        entries.clear()
        keys.clear()
        values.clear()
    }

    override fun isEmpty(): Boolean = holder.keys.isEmpty()

    override fun remove(key: String): T? {
        val namespacedKey = namespaceKey(key)
        val existingValue = holder[namespacedKey, persistentDataType]
        if (existingValue != null) {
            holder.remove(namespacedKey)
            entries.removeIf { it.key == key }
            keys.remove(key)
            values.remove(existingValue)
        }
        return existingValue
    }

    override fun putAll(from: Map<out String, T>) {
        from.forEach { (k, v) ->
            holder[namespaceKey(k), persistentDataType] = v
        }
    }

    override fun put(key: String, value: T): T? {
        val namespaceKey = namespaceKey(key)
        val existingValue = holder[namespaceKey, persistentDataType]
        holder[namespaceKey, persistentDataType] = value
//        val entry = key to value
//        entries.add(entry as MutableMap.MutableEntry<String, T>)
        return existingValue
    }
}

