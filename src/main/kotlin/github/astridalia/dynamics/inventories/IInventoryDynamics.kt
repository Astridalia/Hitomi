package github.astridalia.dynamics.inventories

import github.astridalia.dynamics.CustomDynamicActions
import github.astridalia.dynamics.items.IGUIActionItem

interface IInventoryDynamics {
    var title: String
    var size: Int
    var items: MutableMap<Int, SerializableDynamicInventoryItem>
    var isCancelled: Boolean

    fun toBukkitInventory(): org.bukkit.inventory.Inventory {
        return org.bukkit.Bukkit.createInventory(null, size, title).apply {
            items.forEach { (slot, item) -> setItem(slot, item.toItemStack()) }
        }
    }

    fun open(player: org.bukkit.entity.Player) {
        player.openInventory(toBukkitInventory())
    }

    fun setItem(slot: Int, item: SerializableDynamicInventoryItem) {
        items[slot] = item
    }

    fun setItem(
        slot: Int,
        item: SerializableDynamicInventoryItem,
        listener: (SerializableDynamicInventoryItem) -> Unit
    ) {
        setItem(slot, item)
    }

    fun removeItem(slot: Int) {
        items.remove(slot)
    }

    fun getAction(slot: Int): CustomDynamicActions? {
        return items[slot]?.action
    }

    fun getItemAction(slot: Int): IGUIActionItem? {
        return items[slot]
    }
}