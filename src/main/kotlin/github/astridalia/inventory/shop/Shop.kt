package github.astridalia.inventory.shop

interface Shop  {

    fun purchaseItem(id: String, quantity: Int): Boolean

    fun sellItem(id: String, quantity: Int): Boolean
    fun isItemAvailable(id: String): Boolean
    fun getInventory(): Map<String, Int>

    val shopName: String

    val shopLocation: String

}