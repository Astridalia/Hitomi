package github.astridalia

import co.aikar.commands.PaperCommandManager
import github.astridalia.commands.HitomiCommands
import github.astridalia.dynamics.items.enchantments.events.AutoSmelting
import github.astridalia.dynamics.items.enchantments.events.CubicMiningBlocks
import github.astridalia.dynamics.items.enchantments.events.ExplodingArrow
import github.astridalia.dynamics.items.enchantments.events.SimpleAttackEnchantments
import github.astridalia.dynamics.listeners.DynamicListener
import github.astridalia.events.TestItemsListener
import github.astridalia.mobs.MobManager
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
        single { ExplodingArrow }
        single { CubicMiningBlocks }
        single { SimpleAttackEnchantments }
        single { PaperCommandManager(get()) }

    }

    private val paperCommandManager: PaperCommandManager by inject()
    private val simpleAttackEnchantments: SimpleAttackEnchantments by inject()
    private val explodingArrowEvent: ExplodingArrow by inject()
    private val cubicMiningEvent: CubicMiningBlocks by inject()

    private val testItemsListener: TestItemsListener by inject()

    override fun onEnable() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )

        startKoin {
            modules(appModule)
        }

        paperCommandManager.registerCommand(HitomiCommands)

        registerEventListeners(
            testItemsListener,
            explodingArrowEvent,
            cubicMiningEvent,
            AutoSmelting,
            simpleAttackEnchantments,
            DynamicListener
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