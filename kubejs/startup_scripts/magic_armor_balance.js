// KubeJS 1.21.1 Startup Script - Strip Modded Attributes & Imbue Handlers from Mage Robes
ItemEvents.modification(event => {
    const mageArmors = [
        'irons_spellbooks:pyromancer_helmet',
        'irons_spellbooks:pyromancer_chestplate',
        'irons_spellbooks:pyromancer_leggings',
        'irons_spellbooks:pyromancer_boots',
        'irons_spellbooks:cryomancer_helmet',
        'irons_spellbooks:cryomancer_chestplate',
        'irons_spellbooks:cryomancer_leggings',
        'irons_spellbooks:cryomancer_boots',
        'irons_spellbooks:electromancer_helmet',
        'irons_spellbooks:electromancer_chestplate',
        'irons_spellbooks:electromancer_leggings',
        'irons_spellbooks:electromancer_boots',
        'irons_spellbooks:archevoker_helmet',
        'irons_spellbooks:archevoker_chestplate',
        'irons_spellbooks:archevoker_leggings',
        'irons_spellbooks:archevoker_boots',
        'irons_spellbooks:priest_helmet',
        'irons_spellbooks:priest_chestplate',
        'irons_spellbooks:priest_leggings',
        'irons_spellbooks:priest_boots',
        'irons_spellbooks:cultist_helmet',
        'irons_spellbooks:cultist_chestplate',
        'irons_spellbooks:cultist_leggings',
        'irons_spellbooks:cultist_boots',
        'irons_spellbooks:plagued_helmet',
        'irons_spellbooks:plagued_chestplate',
        'irons_spellbooks:plagued_leggings',
        'irons_spellbooks:plagued_boots',
        'irons_spellbooks:shadowwalker_helmet',
        'irons_spellbooks:shadowwalker_chestplate',
        'irons_spellbooks:shadowwalker_leggings',
        'irons_spellbooks:shadowwalker_boots',
        'irons_spellbooks:wandering_magician_helmet',
        'irons_spellbooks:wandering_magician_chestplate',
        'irons_spellbooks:wandering_magician_leggings',
        'irons_spellbooks:wandering_magician_boots',
        'irons_spellbooks:pumpkin_helmet',
        'irons_spellbooks:pumpkin_chestplate',
        'irons_spellbooks:pumpkin_leggings',
        'irons_spellbooks:pumpkin_boots',
        'wind_spellbooks:aeromancer_helmet',
        'wind_spellbooks:aeromancer_chestplate',
        'wind_spellbooks:aeromancer_leggings',
        'wind_spellbooks:aeromancer_boots'
    ]

    mageArmors.forEach(id => {
        event.modify(id, item => {
            // Cap armor and toughness cleanly
            item.armorToughness = 0.0
            item.knockbackResistance = 0.0
        })
    })
})
