package github.astridalia.dynamics.items.enchantments

interface IEnchant {
    var name: String
    var description: String
    var level: Int
    var maxLevel: Int
    var applicableMaterials: MutableList<String>

    var rarity: Double

    var display: String




    fun toSerialized(): SerializableEnchant = SerializableEnchant(
        name = name,
        description = description,
        level = level,
        maxLevel = maxLevel,
        applicableMaterials = applicableMaterials
    )
}