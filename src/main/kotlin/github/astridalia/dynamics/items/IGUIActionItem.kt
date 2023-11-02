package github.astridalia.dynamics.items

import github.astridalia.dynamics.CustomDynamicActions

interface IGUIActionItem : IItemComponent {
    var action: CustomDynamicActions
}