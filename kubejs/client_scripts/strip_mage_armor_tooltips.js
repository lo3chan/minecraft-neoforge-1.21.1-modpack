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
        event.modify(pattern, tooltip => {
            if (!tooltip) return
            try {
                // In KubeJS 7, tooltip.remove(textPredicate) removes lines matching string or regex
                tooltip.remove(/Spell Power|Max Mana|Mana Regen|Cooldown|Cast Time|Spell Resist|Spell Slots|Active Spell|Right-Click to cast|When on|Slot:|Imbued|Upgrade|Rarity|School Affinity/)
            } catch (err) {
                // Fallback safe iteration if tooltip is list
                if (typeof tooltip.length === 'number') {
                    for (let i = tooltip.length - 1; i >= 0; i--) {
                        let line = tooltip[i]
                        if (line && line.getString) {
                            let s = line.getString()
                            if (s.indexOf('Spell') !== -1 || s.indexOf('Mana') !== -1 || s.indexOf('Cooldown') !== -1) {
                                tooltip.remove(i)
                            }
                        }
                    }
                }
            }
        })
    })
})
