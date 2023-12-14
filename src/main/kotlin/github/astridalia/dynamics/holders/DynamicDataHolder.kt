package github.astridalia.dynamics.holders

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

open class DynamicDataHolder<T>(
    private val namespaceKeyGenerator: (String) -> NamespacedKey,
    private val persistentDataHolder: T.() -> PersistentDataHolder
) {
    fun storeIfNotExists(entity: T, path: String, value: String) {
        val data = entity.persistentDataHolder().persistentDataContainer
        if (data.has(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING)) return
        data.set(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING, value)
    }

    fun store(entity: T, path: String, value: String) {
        val data = entity.persistentDataHolder().persistentDataContainer
        data.set(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING, value)
    }

    fun getOrDefault(entity: T, path: String, default: String): String {
        val data = entity.persistentDataHolder().persistentDataContainer
        return data.get(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING) ?: run {
            data.set(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING, default)
            default
        }
    }

    fun get(entity: T, path: String): String? {
        val data = entity.persistentDataHolder().persistentDataContainer
        return data.get(namespaceKeyGenerator.invoke(path), PersistentDataType.STRING)
    }
}