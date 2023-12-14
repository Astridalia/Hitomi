package github.astridalia.dynamics.holders

import github.astridalia.HitomiPlugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


// TODO: Fix this
object DynamicItemHolder : DynamicDataHolder<ItemStack>(
    { path -> NamespacedKey(JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java), path) },
    { this.itemMeta ?: throw NullPointerException("ItemMeta is null!") }
)