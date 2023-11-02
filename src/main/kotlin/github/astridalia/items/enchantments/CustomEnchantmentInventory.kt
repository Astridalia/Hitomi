package github.astridalia.items.enchantments

import github.astridalia.dynamics.items.SerializableDynamicItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent

object CustomEnchantmentInventory : KoinComponent, Listener {
    val hitomiEnchantInv =
        Bukkit.createInventory(null, InventoryType.CHEST, "${ChatColor.DARK_PURPLE}Hitomi's Enchanting")

    private const val ENCHANTMENT_ITEM_SLOT = 11
    private const val ENCHANTMENT_BOOK_SLOT = 13
    private const val CONFIRMATION_ITEM_ENCHANT = 16

    // /enchant <enchantment> <level> <item>
    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val inventory = e.inventory
        if (inventory !== hitomiEnchantInv) return
        val decoration = SerializableDynamicItem(type = Material.BLACK_STAINED_GLASS_PANE.name, " ")
        (0..<27)
            .filter {
                it !in arrayOf(
                    ENCHANTMENT_BOOK_SLOT,
                    ENCHANTMENT_ITEM_SLOT,
                    CONFIRMATION_ITEM_ENCHANT
                )
            }
            .forEach { hitomiEnchantInv.setItem(it, decoration.toItemStack()) }
        inventory.setItem(
            CONFIRMATION_ITEM_ENCHANT,
            SerializableDynamicItem(type = Material.CYAN_STAINED_GLASS_PANE.name, "Enchant Item").toItemStack()
        )
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val itemBook = e.inventory.getItem(ENCHANTMENT_BOOK_SLOT)
        val item = e.inventory.getItem(ENCHANTMENT_ITEM_SLOT)

        fun returnItemToPlayerSlot(item: ItemStack?, player: Player, slot: Int) {
            item?.let {
                player.inventory.setItem(slot, null)
                val remainingItems = player.inventory.addItem(it)
                for (remainingItem in remainingItems.values)
                    player.location.world?.dropItem(player.location, remainingItem)
            }
        }

        val player = e.player as? Player ?: return
        returnItemToPlayerSlot(itemBook, player, ENCHANTMENT_BOOK_SLOT)
        returnItemToPlayerSlot(item, player, ENCHANTMENT_ITEM_SLOT)
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        val inventory = e.clickedInventory ?: return
        if (inventory !== hitomiEnchantInv) return
        if (e.rawSlot in 0..<27 && e.rawSlot !in arrayOf(
                ENCHANTMENT_BOOK_SLOT,
                ENCHANTMENT_ITEM_SLOT
            )
        ) e.isCancelled = true
        val enchantmentBook = inventory.getItem(ENCHANTMENT_BOOK_SLOT) ?: return
        val itemToEnchant = inventory.getItem(ENCHANTMENT_ITEM_SLOT) ?: return
        if (e.rawSlot != CONFIRMATION_ITEM_ENCHANT) return
        val bookPersistentData = enchantmentBook.itemMeta?.persistentDataContainer ?: return
        val enchantmentKey =
            bookPersistentData.keys.firstNotNullOfOrNull { key -> CustomEnchant.matches(key.key) } ?: return
        if (!itemToEnchant.canEnchant(enchantmentKey)) return
        inventory.setItem(ENCHANTMENT_BOOK_SLOT, null)
        itemToEnchant.enchantOf(enchantmentKey)
    }
}
