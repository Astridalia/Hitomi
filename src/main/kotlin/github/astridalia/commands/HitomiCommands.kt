package github.astridalia.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import github.astridalia.database.RedisCache
import github.astridalia.dynamics.SerializableDynamicInventory
import github.astridalia.dynamics.SerializableDynamicItem
import github.astridalia.items.enchantments.CustomEnchant
import github.astridalia.items.enchantments.CustomEnchantmentInventory
import github.astridalia.items.enchantments.enchantOf
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.litote.kmongo.id.StringId


@CommandAlias("hitomi|hi")
@CommandPermission("hitomi.commands")
object HitomiCommands : BaseCommand() {
    @Subcommand("inventory")
    @CommandPermission("hitomi.admin")
    fun openinv(player: Player) {
        player.closeInventory()
        player.openInventory(CustomEnchantmentInventory.hitomiEnchantInv)
    }

    @CommandAlias("enchant")
    @CommandPermission("hitomi.enchanting.enchant")
    fun enchant(enchantment: String, player: Player) {
        val enchantmentMatches = CustomEnchant.matches(enchantment)
        if (enchantmentMatches == null) {
            player.sendMessage("Enchantment doesn't exist!")
            return
        }
        val itemInUse = player.inventory.itemInMainHand
        itemInUse.enchantOf(enchantmentMatches)
        player.inventory.setItemInMainHand(itemInUse)
        player.sendMessage("Enchanted item in hand.")
    }

    @CommandAlias("item")
    @CommandPermission("hitomi.item")
    fun give(id: String, player: Player) {
        val cachedMongoDBStorage = RedisCache(SerializableDynamicItem::class.java)
        val stringId = StringId<SerializableDynamicItem>(id)
        val itemStack = cachedMongoDBStorage.get(stringId)
        if (itemStack == null) {
            player.sendMessage("Item with ID $id not found.")
            return
        }
        val properties = itemStack.data
        properties.putIfAbsent("owner_id", player.uniqueId.toString())
        properties.putIfAbsent("summoned_on", System.currentTimeMillis().toString())
        properties.putIfAbsent("item_id", itemStack.name)
        val remainingItems = player.inventory.addItem(itemStack.toItemStack())
        if (remainingItems.isNotEmpty()) {
            player.sendMessage("Your inventory is full. Some items could not be added.")
        } else player.sendMessage("You have received the item.")
    }

    @CommandAlias("inventory|inv")
    @CommandPermission("hitomi.admin")
    fun openInventory(inventory: String, player: Player) {
        val cachedMongoDBStorage = RedisCache(SerializableDynamicInventory::class.java)
        val itemStack = cachedMongoDBStorage.get(StringId(inventory))
        if (itemStack == null) {
            player.sendMessage("Inventory with ID $inventory not found.")
            return
        }
        player.openInventory(itemStack.toBukkitInventory())
    }

    @CommandAlias("data")
    @CommandPermission("hitomi.admin")
    fun addData(player: Player) {
        val itemInUse = player.inventory.itemInMainHand
        val container = itemInUse.itemMeta?.persistentDataContainer ?: return
        val containers = container.keys.associateWith { key ->
            container.get(key, PersistentDataType.STRING)
        }
        val message = buildString {
            appendLine("Data in containers:")
            containers.forEach { (key, value) ->
                appendLine("$key: ${value.toString()}")
            }
        }
        player.sendMessage(message)
    }


}