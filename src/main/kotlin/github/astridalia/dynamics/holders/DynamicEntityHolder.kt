package github.astridalia.dynamics.holders

import github.astridalia.HitomiPlugin
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin


object DynamicEntityHolder : DynamicDataHolder<Entity>(
    { path -> NamespacedKey(JavaPlugin.getProvidingPlugin(HitomiPlugin::class.java), path) },
    { this }
)