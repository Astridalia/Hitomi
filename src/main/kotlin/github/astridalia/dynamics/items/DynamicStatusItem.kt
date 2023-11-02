package github.astridalia.dynamics.items

import github.astridalia.dynamics.DynamicStats

interface DynamicStatusItem {
    var cooldowns: MutableMap<String, Long>
    var modifiers: MutableMap<String, Double>
    var stats: DynamicStats
}