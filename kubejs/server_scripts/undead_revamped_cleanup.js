// KubeJS Server Script - The Undead Revamped Vanilla Loot & Item Filter
ServerEvents.recipes(event => {
    // Remove all custom crafting recipes for bostrox, inducer, and custom mod items
    event.remove({ mod: 'undead_revamp2' })
})

LootJS.modifiers(event => {
    // If LootJS or direct loot modification is used
})
