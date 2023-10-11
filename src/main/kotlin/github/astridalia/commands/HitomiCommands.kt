package github.astridalia.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import github.astridalia.database.RedisCache
import github.astridalia.items.SerializedItemStack
import github.astridalia.items.enchantments.CustomEnchant
import github.astridalia.items.enchantments.CustomEnchantmentInventory
import github.astridalia.items.enchantments.enchantOf
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

    @Subcommand("enchant")
    @CommandPermission("hitomi.enchanting.summon")
    fun summonEnchant(id: String, level: Int, player: Player) {
        val matches = CustomEnchant.matches(id)
        if (matches == null) {
            player.sendMessage("Enchantment doesn't exist!")
        } else {
            val itemStack = ItemStack(Material.ENCHANTED_BOOK, 1)
            itemStack.enchantOf(matches)
            val remainingItems = player.inventory.addItem(itemStack)
            if (remainingItems.isNotEmpty()) {
                player.sendMessage("Your inventory is full. Some items could not be added.")
            } else player.sendMessage("You have received the item.")

        }
    }

    @CommandAlias("item")
    @CommandPermission("hitomi.item")
    fun give(id: String, player: Player) {
        val cachedMongoDBStorage = RedisCache(SerializedItemStack::class.java, "itemStacks")
        val stringId = StringId<SerializedItemStack>(id)
        val itemStack = cachedMongoDBStorage.get(stringId)
        if (itemStack == null) {
            player.sendMessage("Item with ID $id not found.")
            return
        }
        val properties = itemStack.persistentData
        properties.putIfAbsent("owner_id", player.uniqueId.toString())
        properties.putIfAbsent("summoned_on", System.currentTimeMillis().toString())
        properties.putIfAbsent("item_id", itemStack._id)
        val remainingItems = player.inventory.addItem(itemStack.toItemStack())
        if (remainingItems.isNotEmpty()) {
            player.sendMessage("Your inventory is full. Some items could not be added.")
        } else player.sendMessage("You have received the item.")
    }

    @CommandAlias("data")
    @CommandPermission("hitomi.admin")
    fun addData(player: Player) {
        val itemInUse = player.inventory.itemInMainHand
        val container = itemInUse.itemMeta?.persistentDataContainer ?: return
        val containers = container.keys.associateWith {
            container.get(it, PersistentDataType.STRING)
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