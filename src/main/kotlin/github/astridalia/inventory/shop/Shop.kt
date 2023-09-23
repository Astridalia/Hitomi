package github.astridalia.inventory.shop

import kotlinx.serialization.SerialName
import org.bukkit.Location

interface Shop {

    fun purchaseItem(id: String, quantity: Int): Boolean

    fun sellItem(id: String, quantity: Int): Boolean
    fun isItemAvailable(id: String): Boolean
    fun getInventory(): Map<String, Int>

    @SerialName("_id")
    val id: String

    val location: Location

}