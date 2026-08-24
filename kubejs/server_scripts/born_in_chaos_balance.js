// KubeJS Server Script - Born in Chaos Balance & Aether Spawn Filter
ServerEvents.recipes(event => {
    let opRecipes = ['born_in_chaos_v1:armor_plate_from_dark_metal_k', 'born_in_chaos_v1:armor_plate_from_dark_metal_k_2', 'born_in_chaos_v1:armor_plate_from_dark_metal_k_3', 'born_in_chaos_v1:armor_plate_from_dark_metal_k_4', 'born_in_chaos_v1:armor_plate_from_dark_metal_k_5', 'born_in_chaos_v1:darkupgradek', 'born_in_chaos_v1:dark_atrium_craft', 'born_in_chaos_v1:dark_ritual_dagger_k', 'born_in_chaos_v1:death_totem_k', 'born_in_chaos_v1:frostbitten_blade_craft', 'born_in_chaos_v1:great_dark_crusher_k', 'born_in_chaos_v1:great_reaper_ax_k', 'born_in_chaos_v1:houndtrapk', 'born_in_chaos_v1:intoxicating_dagger_k', 'born_in_chaos_v1:nightmare_boots_k', 'born_in_chaos_v1:nightmare_mask_k', 'born_in_chaos_v1:nightmare_pantsk', 'born_in_chaos_v1:nightmare_robe_k', 'born_in_chaos_v1:nightmare_scythe_k', 'born_in_chaos_v1:nut_hammer_craft', 'born_in_chaos_v1:seedof_chaos_k_1', 'born_in_chaos_v1:shellmace_k', 'born_in_chaos_v1:skull_crusher_k', 'born_in_chaos_v1:soul_saber_k', 'born_in_chaos_v1:spider_bite_craft', 'born_in_chaos_v1:spinyshelltrap_k', 'born_in_chaos_v1:spiny_shell_chestplate_k', 'born_in_chaos_v1:spiny_shell_helm_k', 'born_in_chaos_v1:spiritual_divider_k', 'born_in_chaos_v1:staffofthe_summoner_k', 'born_in_chaos_v1:stormcallers_horn_craft', 'born_in_chaos_v1:sweet_axe_craft', 'born_in_chaos_v1:sweet_sword_craft']
    for (let rId of opRecipes) {
        event.remove({ id: rId })
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
