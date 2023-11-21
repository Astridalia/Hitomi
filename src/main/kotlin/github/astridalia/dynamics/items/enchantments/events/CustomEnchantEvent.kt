package github.astridalia.dynamics.items.enchantments.events

import github.astridalia.dynamics.items.enchantments.SerializableEnchant
import org.bukkit.entity.Entity
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class CustomEnchantEvent<T>(
    val player: Entity,
    val enchantedItem: SerializableEnchant,
    val action: T
) : Event(), Cancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}