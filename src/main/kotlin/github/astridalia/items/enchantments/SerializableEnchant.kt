package github.astridalia.items.enchantments

import kotlinx.serialization.Serializable


@Serializable
data class SerializableEnchant(
    override var name: String,
    override var description: String,
    override var level: Int,
    override var maxLevel: Int,
    override var applicableMaterials: MutableList<String>
): IEnchant
