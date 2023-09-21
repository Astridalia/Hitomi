package github.astridalia

import github.astridalia.database.MongoStorage
import github.astridalia.events.TestItemsListener
import github.astridalia.items.CustomEnchantments
import github.astridalia.items.SerializedItemStack
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.litote.kmongo.id.StringId


class HitomiPlugin : JavaPlugin(), KoinComponent {
    private val appModule = module {
        single<JavaPlugin> { this@HitomiPlugin }
        single { TestItemsListener() }
        single { CustomEnchantments() }

    }

    private val testItemsListener: TestItemsListener by inject()

    override fun onEnable() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )

        startKoin {
            modules(appModule)
        }

        val itemStorage = MongoStorage(SerializedItemStack::class.java, "test", "items")
        val itemStringId = StringId<SerializedItemStack>("testing_items")
        itemStorage.get(itemStringId) ?: run {
            val itemEntity = SerializedItemStack(
                "STONE", 1, 2, "rare", mutableListOf()
            )
            itemStorage.insertOrUpdate(itemStringId, itemEntity)
            itemEntity
        }
        Bukkit.getPluginManager().registerEvents(testItemsListener, this)
    }

    override fun onDisable() {
        stopKoin()
    }
}