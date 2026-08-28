// KubeJS 1.21.1 Client Script - Strip Modded Stats, Spell Power, Mana, and RPG Tooltips
ItemEvents.modifyTooltips(event => {
    // 1. All Mage Robes & Armor
    const mageArmors = [
        /irons_spellbooks:.*_helmet/,
        /irons_spellbooks:.*_chestplate/,
        /irons_spellbooks:.*_leggings/,
        /irons_spellbooks:.*_boots/,
        /irons_spellbooks:.*_hat/,
        /irons_spellbooks:.*_crown/,
        /wind_spellbooks:aeromancer_.*/
    ]

    // 2. Modded Weapons & Unique Artifacts
    const moddedWeapons = [
        /irons_spellbooks:.*_sword/,
        /irons_spellbooks:.*_blade/,
        /irons_spellbooks:.*_scythe/,
        /irons_spellbooks:.*_flamberge/,
        /irons_spellbooks:.*_dagger/,
        /irons_spellbooks:.*_staff/,
        /irons_spellbooks:.*_wand/,
        'irons_spellbooks:firebrand',
        'irons_spellbooks:dreadsword',
        'irons_spellbooks:truthseeker',
        'irons_spellbooks:spellbreaker',
        'irons_spellbooks:magehunter',
        'irons_spellbooks:hellrazor',
        'irons_spellbooks:twilight_gale',
        'wind_spellbooks:wind_staff'
    ]

    const allPatterns = mageArmors.concat(moddedWeapons)

    allPatterns.forEach(pattern => {
        event.modify(pattern, (item, text) => {
            for (let i = text.length - 1; i >= 1; i--) {
                let lineStr = text.get(i).getString()
                if (
                    lineStr.includes('Spell Power') ||
                    lineStr.includes('Max Mana') ||
                    lineStr.includes('Mana Regen') ||
                    lineStr.includes('Cooldown') ||
                    lineStr.includes('Cast Time') ||
                    lineStr.includes('Spell Resist') ||
                    lineStr.includes('Spell Slots') ||
                    lineStr.includes('Active Spell') ||
                    lineStr.includes('Right-Click to cast') ||
                    lineStr.includes('When on') ||
                    lineStr.includes('Slot:') ||
                    lineStr.includes('Imbued') ||
                    lineStr.includes('Upgrade') ||
                    lineStr.includes('Rarity') ||
                    lineStr.includes('School Affinity')
                ) {
                    text.remove(i)
                }
            }
        })
    })
})
