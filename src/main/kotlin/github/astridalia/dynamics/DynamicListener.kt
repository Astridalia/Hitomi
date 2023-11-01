package github.astridalia.dynamics

import github.astridalia.database.RedisCache
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType
import org.litote.kmongo.id.StringId

object DynamicListener : Listener {
    private val dynamicInventory = RedisCache(SerializableDynamicInventory::class.java)

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) {
        val inventory = dynamicInventory.get(StringId(event.view.title)) ?: return
        dynamicInventory.listenForChanges {
            val viewers = ArrayList(event.viewers)
            viewers.forEach {
                it.closeInventory()
            }

        }
        inventory.items.forEach {
            event.inventory.setItem(it.key, it.value.toItemStack())
        }
    }

    @EventHandler
    fun onCloseInventory(event: InventoryCloseEvent) {
        val inventory = dynamicInventory.get(StringId(event.view.title)) ?: return
        val player = event.player as? Player ?: return
        dynamicInventory.listenForChanges {
            val viewers = ArrayList(event.viewers)
            //close all viewers
            viewers.forEach {
                it.closeInventory()
            }
        }
        if (!inventory.isCancelled) return
        event.inventory.clear()
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {

        val inventory = dynamicInventory.get(StringId(event.view.title)) ?: return
        val item = inventory.getItemAction(event.slot) ?: return
        event.isCancelled = inventory.isCancelled
        val persistentData = event.currentItem?.itemMeta?.persistentDataContainer ?: return
        val player = event.whoClicked as? Player ?: return
        dynamicInventory.listenForChanges {
            event.viewers.forEach {
                it.closeInventory()
            }
        }
        when (item.action) {
            CustomDynamicActions.CLOSE -> event.whoClicked.closeInventory()
            CustomDynamicActions.EXECUTE -> {
                persistentData.get(item.namespaceKey("command"), PersistentDataType.STRING)?.let {
                    player.server.dispatchCommand(event.whoClicked, it)
                }
            }

            CustomDynamicActions.NONE -> return
            CustomDynamicActions.OPEN -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.open(player)
                }
            }

            CustomDynamicActions.NEXT_PAGE -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.open(player)
                }
            }

            CustomDynamicActions.PREVIOUS_PAGE -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.open(player)
                }
            }
        }
    }
}