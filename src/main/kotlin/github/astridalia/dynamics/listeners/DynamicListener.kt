package github.astridalia.dynamics.listeners

import github.astridalia.database.RedisCache
import github.astridalia.dynamics.CustomDynamicActions
import github.astridalia.dynamics.inventories.SerializableDynamicInventory
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.persistence.PersistentDataType
import org.litote.kmongo.id.StringId

object DynamicListener : Listener {
    private val dynamicInventory = RedisCache(SerializableDynamicInventory::class.java)

    private fun closeViewersInventory(viewers: Collection<HumanEntity>) {
        viewers.forEach(HumanEntity::closeInventory)
    }

    private fun updateInventoryFromDynamic(inventory: Inventory, dynamicInventory: SerializableDynamicInventory) {
        dynamicInventory.items.forEach { (slot, item) ->
            inventory.setItem(slot, item.toItemStack())
        }
    }

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) {
        dynamicInventory.listenForChanges { closeViewersInventory(event.viewers) }
        dynamicInventory.get(StringId(event.view.title))?.let {
            updateInventoryFromDynamic(event.inventory, it)
        }
    }

    @EventHandler
    fun onCloseInventory(event: InventoryCloseEvent) {
        dynamicInventory.get(StringId(event.view.title))?.let {
            if (it.isCancelled) {
                event.inventory.clear()
            }
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val inventory = dynamicInventory.get(StringId(event.view.title)) ?: return
        val item = inventory.getItemAction(event.slot) ?: return

        event.isCancelled = inventory.isCancelled

        val persistentData = event.currentItem?.itemMeta?.persistentDataContainer ?: return
        val player = event.whoClicked as? Player ?: return
        val inventoryId = persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)

        when (item.action) {
            CustomDynamicActions.CLOSE -> player.closeInventory()
            CustomDynamicActions.EXECUTE -> {
                persistentData.get(item.namespaceKey("command"), PersistentDataType.STRING)?.let {
                    player.server.dispatchCommand(player, it)
                }
            }

            CustomDynamicActions.OPEN, CustomDynamicActions.NEXT_PAGE, CustomDynamicActions.PREVIOUS_PAGE -> {
                inventoryId?.let { dynamicInventory.get(StringId(inventoryId))?.open(player) }
            }

            else -> return
        }
    }
}