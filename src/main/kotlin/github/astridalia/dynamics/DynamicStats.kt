package github.astridalia.dynamics

import github.astridalia.dynamics.items.SerializableDynamicStatItem

interface DynamicStats {
    var strength: Int
    var intellect: Int
    var agility: Int
    var will: Int
    var power: Int


    var EquipmentSet: MutableSet<SerializableDynamicStatItem>
}