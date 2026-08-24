// KubeJS Server Script - The Undead Revamped Recipe Filter
ServerEvents.recipes(event => {
    // Remove custom crafting recipes for undead_revamp2 items
    event.remove({ mod: 'undead_revamp2' })
})
