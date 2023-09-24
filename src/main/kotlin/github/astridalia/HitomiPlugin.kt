package github.astridalia

import co.aikar.commands.PaperCommandManager
import github.astridalia.commands.HitomiCommands
import github.astridalia.commands.wand.WandCommand
import github.astridalia.events.TestItemsListener
import github.astridalia.items.SerializedItemStack
import github.astridalia.items.enchantments.CustomEnchantmentInventory
import github.astridalia.items.enchantments.CustomEnchantments
import github.astridalia.items.enchantments.events.CubicMiningBlocks
import github.astridalia.items.enchantments.events.ExplodingArrow
import github.astridalia.items.enchantments.events.SimpleAttackEnchantments
import github.astridalia.mobs.MobManager
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


class HitomiPlugin : JavaPlugin(), KoinComponent {
    private val appModule = module {
        single<JavaPlugin> { this@HitomiPlugin }
        single<Plugin> { this@HitomiPlugin }
        single { TestItemsListener() }
        single { CustomEnchantments }
        single { CustomEnchantmentInventory }
        single { ExplodingArrow }
        single { CubicMiningBlocks }
        single { SimpleAttackEnchantments }
        single { SerializedItemStack }
        single { PaperCommandManager(get()) }
    }

    private val paperCommandManager: PaperCommandManager by inject()
    private val simpleAttackEnchantments: SimpleAttackEnchantments by inject()
    private val explodingArrowEvent: ExplodingArrow by inject()
    private val cubicMiningEvent: CubicMiningBlocks by inject()

    private val testItemsListener: TestItemsListener by inject()
    private val customEnchantmentInventory: CustomEnchantmentInventory by inject()

    override fun onEnable() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )

        startKoin {
            modules(appModule)
        }

        paperCommandManager.registerCommand(HitomiCommands)
        paperCommandManager.registerCommand(WandCommand)

        registerEventListeners(
            testItemsListener,
            customEnchantmentInventory,
            explodingArrowEvent,
            cubicMiningEvent,
            WandCommand,
            simpleAttackEnchantments
        )
    }

    private fun registerEventListeners(vararg listeners: Listener) {
        val pluginManager = server.pluginManager
        listeners.forEach { listener ->
            pluginManager.registerEvents(listener, this)
        }
    }


    override fun onDisable() {
        MobManager.cleanUp()
        stopKoin()
    }
}