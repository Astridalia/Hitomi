package github.astridalia.character.currency

import kotlinx.serialization.Serializable


@Serializable
data class CharacterCurrency(override val id: String, override var balance: Int) : Currency {
    override fun deposit(amount: Int) {
        if (amount > 0) balance += amount
    }

    override fun withdraw(amount: Int): Boolean {
        if (amount in 1..balance) {
            balance -= amount
            return true
        }
        return false
    }
}
