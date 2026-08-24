// KubeJS Server Script - Restrict The Undead Revamped Mobs to Overworld Only
EntityEvents.spawned(event => {
    let entity = event.entity
    let type = entity.type
    if (type.startsWith('undead_revamp2:')) {
        let dim = event.level.dimension.location().toString()
        // If spawning in The Nether, The Aether, or The End, cancel the spawn immediately
        if (dim.includes('nether') || dim.includes('aether') || dim.includes('the_end') || dim !== 'minecraft:overworld') {
            event.cancel()
        }
    }
})
