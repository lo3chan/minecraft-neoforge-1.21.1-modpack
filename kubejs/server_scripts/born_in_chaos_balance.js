// KubeJS Server Script - Born in Chaos Balance & Aether Spawn Filter
ServerEvents.recipes(event => {
    let opItems = [
        'born_in_chaos_v1:great_dark_crusher',
        'born_in_chaos_v1:great_reaper_ax',
        'born_in_chaos_v1:nightmare_scythe',
        'born_in_chaos_v1:skull_crusher',
        'born_in_chaos_v1:soul_saber',
        'born_in_chaos_v1:dark_ritual_dagger',
        'born_in_chaos_v1:intoxicating_dagger',
        'born_in_chaos_v1:sharpened_darketal_sword',
        'born_in_chaos_v1:death_totem',
        'born_in_chaos_v1:staffofthe_summoner',
        'born_in_chaos_v1:stormcallers_horn',
        'born_in_chaos_v1:houndtrap',
        'born_in_chaos_v1:spinyshelltrap',
        'born_in_chaos_v1:nut_hammer',
        'born_in_chaos_v1:sweet_axe',
        'born_in_chaos_v1:sweet_sword',
        'born_in_chaos_v1:frostbitten_blade',
        'born_in_chaos_v1:shellmace',
        'born_in_chaos_v1:spider_bite',
        'born_in_chaos_v1:spiritual_divider',
        'born_in_chaos_v1:nightmare_mask',
        'born_in_chaos_v1:nightmare_robe',
        'born_in_chaos_v1:nightmare_pants',
        'born_in_chaos_v1:nightmare_boots',
        'born_in_chaos_v1:spiny_shell_helm',
        'born_in_chaos_v1:spiny_shell_chestplate',
        'born_in_chaos_v1:armor_plate_from_dark_metal',
        'born_in_chaos_v1:darkupgrade'
    ]
    for (let id of opItems) {
        event.remove({ output: id })
    }
})

EntityEvents.spawned(event => {
    let entity = event.entity
    if (!entity) return
    let type = entity.type
    if (type && type.startsWith('born_in_chaos_v1:')) {
        let dim = '' + event.level.dimension
        if (dim.includes('aether')) {
            event.cancel()
        }
    }
})
