package github.astridalia.dynamics

interface IInventoryDynamics {
    var title: String
    var size: Int
    var items: MutableMap<Int, SerializableDynamicInventoryItem>
    var isCancelled: Boolean

    fun toBukkitInventory(): org.bukkit.inventory.Inventory {
        val inventory = org.bukkit.Bukkit.createInventory(null, size, title)
        items.forEach { (slot, item) ->
            inventory.setItem(
                slot, SerializableDynamicItem(
                    type = item.type,
                    name = item.name,
                    lore = item.lore,
                    data = item.data,
                    model = item.model
                ).toItemStack()
            )
        }
        return inventory
    }

    fun open(player: org.bukkit.entity.Player) {
        player.openInventory(toBukkitInventory())
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

    fun setItem(slot: Int, item: SerializableDynamicInventoryItem) {
        items[slot] = item
    }

    fun getAction(slot: Int): CustomDynamicActions? {
        return items[slot]?.action
    }

    fun getItemAction(slot: Int): IGUIActionItem? {
        return items[slot]
    }

}