package github.astridalia

import co.aikar.commands.PaperCommandManager
import github.astridalia.commands.SummonItem
import github.astridalia.events.TestItemsListener
import github.astridalia.items.enchantments.CustomEnchantmentInventory
import github.astridalia.items.enchantments.CustomEnchantments
import github.astridalia.mobs.MobManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


class HitomiPlugin : JavaPlugin(), KoinComponent {

    private lateinit var commandManager: PaperCommandManager

    private val appModule = module {
        single<JavaPlugin> { this@HitomiPlugin }
        single { TestItemsListener() }
        single { CustomEnchantments }
        single { CustomEnchantmentInventory }

    }

    private val testItemsListener: TestItemsListener by inject()
    private val customEnchantmentInventory: CustomEnchantmentInventory by inject()

    override fun onEnable() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )

        commandManager = PaperCommandManager(this)
        commandManager.registerCommand(SummonItem)

        startKoin {
            modules(appModule)
        }

        Bukkit.getPluginManager().registerEvents(testItemsListener, this)
        Bukkit.getPluginManager().registerEvents(customEnchantmentInventory,this)
    }

    override fun onDisable() {
        MobManager.cleanUp()
        stopKoin()
    }
}