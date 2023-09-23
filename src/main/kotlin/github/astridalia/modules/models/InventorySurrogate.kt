package github.astridalia.modules.models

import github.astridalia.database.CachedMongoDBStorage
import github.astridalia.items.SerializedItemStack
import github.astridalia.items.toSerialized
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.litote.kmongo.id.StringId
import java.util.UUID



// TODO: BROKEN ASF
@Serializable
data class InventorySurrogate(
    val _id: String = UUID.randomUUID().toString(),
    val type: Int = InventoryType.CHEST.defaultSize,
    val name: String = "test",
    var items: ArrayList<SerializedItemStack> = arrayListOf()
) {
    fun toInventory(): Inventory {
        val inventory = Bukkit.createInventory(null, type, name)
        inventory.contents = items.map { it.toItemStack() }.toList().toTypedArray()
        return inventory
    }

    fun storeToDatabase(inventory: Inventory, name: String): Inventory {
        val cachedMongoDBStorage = CachedMongoDBStorage(InventorySurrogate::class.java, "inventories")
        val stringId = StringId<InventorySurrogate>("test")
        val invSurrogate = cachedMongoDBStorage.get(stringId)

        if (invSurrogate != null) {
            // Update existing InventorySurrogate
            invSurrogate.items.clear()
            inventory.forEachIndexed { index, itemStack ->
                invSurrogate.items[index] = itemStack.toSerialized()
            }
            cachedMongoDBStorage.insertOrUpdate(stringId, invSurrogate)
        } else {
            // Create a new InventorySurrogate
            val newInvSurrogate = inventory.toSerialize(name)
            cachedMongoDBStorage.insertOrUpdate(stringId, newInvSurrogate)
        }

        // Check if invSurrogate is not null before calling toInventory()
        return invSurrogate?.toInventory() ?: inventory
    }


}

fun Inventory.toSerialize(name: String): InventorySurrogate {
    val serializable = InventorySurrogate(
        type = this.type.defaultSize,
        name = name
    )
    val serializedItemStacks = this.contents.map { it.toSerialized() }.toList() as ArrayList<SerializedItemStack>
    serializable.items = serializedItemStacks

    return serializable
}
