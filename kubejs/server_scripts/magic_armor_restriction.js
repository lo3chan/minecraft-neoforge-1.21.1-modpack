// KubeJS Server Script - Magic Armor Restriction & Spell Power Neutralizer
// Neutralizes all Iron's Spells custom spell power and mana attributes from armor

PlayerEvents.tick(event => {
    let player = event.player
    if (!player || player.age % 20 !== 0) return // Check every 1 second

    // If player has any active irons_spellbooks attribute modifiers, remove them
    const ironsAttributes = [
        'irons_spellbooks:fire_spell_power',
        'irons_spellbooks:ice_spell_power',
        'irons_spellbooks:lightning_spell_power',
        'irons_spellbooks:holy_spell_power',
        'irons_spellbooks:ender_spell_power',
        'irons_spellbooks:blood_spell_power',
        'irons_spellbooks:evocation_spell_power',
        'irons_spellbooks:nature_spell_power',
        'irons_spellbooks:eldritch_spell_power',
        'irons_spellbooks:max_mana',
        'irons_spellbooks:mana_regen',
        'irons_spellbooks:cooldown_reduction',
        'irons_spellbooks:cast_time_reduction',
        'irons_spellbooks:spell_resist'
    ]

    ironsAttributes.forEach(attrId => {
        try {
            let instance = player.getAttribute(attrId)
            if (instance) {
                let base = instance.getBaseValue()
                // Clear any non-base modifiers
                let mods = instance.getModifiers()
                if (mods && mods.size() > 0) {
                    let toRemove = []
                    mods.forEach(m => toRemove.push(m))
                    toRemove.forEach(m => instance.removeModifier(m.id()))
                }
            }
        } catch (e) {}
    })
})
