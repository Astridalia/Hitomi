package github.astridalia.items.enchantments

import github.astridalia.items.SerializedItemStack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent


object CustomEnchantmentInventory : KoinComponent, Listener {

    val enchantInventory = Bukkit.createInventory(null, InventoryType.CHEST, "Hitomi's Enchanting")

    private const val ENCHANTMENT_ITEM_SLOT = 11
    private const val ENCHANTMENT_BOOK_SLOT = 13
    private const val ITEM_PROTECTION_SLOT = 15
    private const val CONFIRMATION_ITEM_ENCHANT = 16

    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val inventory = e.inventory
        if (inventory !== enchantInventory) return
        val decoration = SerializedItemStack(type = Material.BLACK_STAINED_GLASS_PANE.name)
        (0..<27)
            .filter { it !in arrayOf(ENCHANTMENT_BOOK_SLOT, ENCHANTMENT_ITEM_SLOT, ITEM_PROTECTION_SLOT, CONFIRMATION_ITEM_ENCHANT) }
            .forEach { enchantInventory.setItem(it, decoration.toItemStack()) }
        inventory.setItem(CONFIRMATION_ITEM_ENCHANT, SerializedItemStack(type = Material.BAMBOO_BUTTON.name).toItemStack())
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val itemBook = e.inventory.getItem(ENCHANTMENT_BOOK_SLOT)
        val itemProtection = e.inventory.getItem(ITEM_PROTECTION_SLOT)
        val item = e.inventory.getItem(ENCHANTMENT_ITEM_SLOT)

        fun returnItemToPlayerSlot(item: ItemStack?, player: Player, slot: Int) {
            item?.let {
                e.inventory.setItem(slot, null)
                player.inventory.addItem(it)
            }
        }

        val player = e.player as? Player ?: return
        returnItemToPlayerSlot(itemBook, player, ENCHANTMENT_BOOK_SLOT)
        returnItemToPlayerSlot(itemProtection, player, ITEM_PROTECTION_SLOT)
        returnItemToPlayerSlot(item, player, ENCHANTMENT_ITEM_SLOT)
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        val inventory = e.clickedInventory ?: return
        if (inventory !== enchantInventory) return
        if (e.rawSlot in 0..<27 && e.rawSlot !in arrayOf(ENCHANTMENT_BOOK_SLOT, ENCHANTMENT_ITEM_SLOT, ITEM_PROTECTION_SLOT))
            e.isCancelled = true
        val player = e.whoClicked as? Player ?: return
        val enchantmentBook = inventory.getItem(ENCHANTMENT_BOOK_SLOT)
        val itemToEnchant = inventory.getItem(ENCHANTMENT_ITEM_SLOT) ?: return
        if (e.rawSlot != CONFIRMATION_ITEM_ENCHANT || enchantmentBook == null) return
        enchantmentBook.itemMeta?.persistentDataContainer?.keys?.forEach { key ->
            val enchantment = HyperionEnchantments.matches(key.key) ?: return@forEach
            if (!enchantment.isRightMaterial(enchantment, itemToEnchant.type)) {
                player.sendMessage("Sorry, this item can't be enchanted, it must be type ${enchantment.applicableMaterials.first().name}")
                return@forEach
            }
            e.inventory.setItem(ENCHANTMENT_BOOK_SLOT, null)
            e.inventory.setItem(ITEM_PROTECTION_SLOT, null)
            CustomEnchantments.applyTo(itemStack = itemToEnchant, hyperionEnchantments = enchantment)
        }
    }
}
