package github.astridalia.dynamics.items

import github.astridalia.dynamics.DynamicStats

data class SerializableDynamicStatItem(
    override var strength: Int = 0,
    override var intellect: Int = 0,
    override var agility: Int = 0,
    override var will: Int = 0,
    override var power: Int = 0,
    override var type: String,
    override var name: String,
    override var lore: MutableList<String> = mutableListOf(),
    override var data: MutableMap<String, String> = mutableMapOf(
        "DateGeneratedBy" to "${System.currentTimeMillis()}",
        "power" to "$power",
        "will" to "$will",
        "agility" to "$agility",
        "intellect" to "$intellect",
        "strength" to "$strength",
    ),
    override var model: Int = 0,
    override var EquipmentSet: MutableSet<SerializableDynamicStatItem> = mutableSetOf()
) : IItemComponent, DynamicStats