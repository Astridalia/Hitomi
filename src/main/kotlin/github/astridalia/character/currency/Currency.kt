package github.astridalia.character.currency

interface Currency {
    val id: String
    val balance: Int

    fun deposit(amount: Int)
    fun withdraw(amount: Int): Boolean
}