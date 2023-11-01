package github.astridalia.dynamics

import github.astridalia.database.RedisCache
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType
import org.litote.kmongo.id.StringId

object DynamicListener : Listener {
    private val dynamicInventory = RedisCache(SerializableDynamicInventory::class.java)

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val inventory = dynamicInventory.get(StringId(event.view.title)) ?: return
        val item = inventory.getItemAction(event.slot) ?: return
        event.isCancelled = inventory.isCancelled
        val persistentData = event.currentItem?.itemMeta?.persistentDataContainer ?: return
        when (item.action) {
            CustomDynamicActions.CLOSE -> event.whoClicked.closeInventory()
            CustomDynamicActions.EXECUTE -> {
                persistentData.get(item.namespaceKey("command"), PersistentDataType.STRING)?.let {
                    event.whoClicked.server.dispatchCommand(event.whoClicked, it)
                }
            }

            CustomDynamicActions.NONE -> return
            CustomDynamicActions.OPEN -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.toBukkitInventory()
                        ?.let { it1 -> event.whoClicked.openInventory(it1) }
                }
            }

            CustomDynamicActions.NEXT_PAGE -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.toBukkitInventory()
                        ?.let { it1 -> event.whoClicked.openInventory(it1) }
                }
            }

            CustomDynamicActions.PREVIOUS_PAGE -> {
                persistentData.get(item.namespaceKey("inventory"), PersistentDataType.STRING)?.let {
                    dynamicInventory.get(StringId(it))?.toBukkitInventory()
                        ?.let { it1 -> event.whoClicked.openInventory(it1) }
                }
            }
        }
    }
}