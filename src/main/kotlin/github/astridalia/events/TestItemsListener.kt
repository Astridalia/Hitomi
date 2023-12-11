package github.astridalia.events

import github.astridalia.database.RedisCache
import github.astridalia.dynamics.inventories.SerializableDynamicInventory
import github.astridalia.dynamics.inventories.SerializableDynamicInventoryItem
import github.astridalia.dynamics.items.SerializableDynamicItem
import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.litote.kmongo.id.StringId

class TestItemsListener : Listener, KoinComponent {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        val inventorySerialization = RedisCache(SerializableDynamicInventory::class.java)
        val inventoryId = StringId<SerializableDynamicInventory>("test")
        inventorySerialization.get(inventoryId) ?: run {
            val inventoryEntity = SerializableDynamicInventory(
                title = "test",
                size = 9,
                items = mutableMapOf(
                    0 to SerializableDynamicInventoryItem(
                        type = Material.DIAMOND_SWORD,
                        name = "test_item",
                        lore = mutableListOf(),
                        model = 0
                    )
                )
            )
            inventorySerialization.insertOrUpdate(inventoryId, inventoryEntity)
            inventoryEntity
        }

        val newEnchantSerializer = RedisCache(SerializableEnchant::class.java)
        val enchantId = StringId<SerializableEnchant>("fiery")
        newEnchantSerializer.get(enchantId) ?: run {
            val enchantEntity = SerializableEnchant(
                name = "fiery",
                level = 1,
                maxLevel = 5,
                description = "Sets the target on fire for a period of time."
            )
            newEnchantSerializer.insertOrUpdate(enchantId, enchantEntity)
            enchantEntity
        }

        // TODO: adding in more upgrades soon!
        val dynamicItem = RedisCache(SerializableDynamicItem::class.java)
        val stringId = StringId<SerializableDynamicItem>("test")
        val item = dynamicItem.get(stringId) ?: run {
            val itemEntity = SerializableDynamicItem(
                type = Material.DIAMOND_SWORD,
                name = "test_item",
                lore = mutableListOf(),
                model = 0
            )
            dynamicItem.insertOrUpdate(stringId, itemEntity)
            itemEntity
        }
        player.inventory.addItem(item.toItemStack())

    }


}