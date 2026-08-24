// KubeJS Server Script - Restrict The Undead Revamped Mobs to Overworld Only
EntityEvents.spawned(event => {
    let entity = event.entity
    if (!entity) return
    let type = entity.type
    if (type && type.startsWith('undead_revamp2:')) {
        let dim = '' + event.level.dimension
        if (dim.includes('nether') || dim.includes('aether') || dim.includes('the_end') || dim !== 'minecraft:overworld') {
            event.cancel()
        }
    }
})
