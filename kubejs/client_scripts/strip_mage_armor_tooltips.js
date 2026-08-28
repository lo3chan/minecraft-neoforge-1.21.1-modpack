// KubeJS 1.21.1 Client Script - Strip Modded Stats, Spell Power, Mana, and RPG Tooltips
ItemEvents.modifyTooltips(event => {
    // Clean all Iron's Spells and Wind's Spellbooks armors, weapons, and staves dynamically
    const patterns = [
        /irons_spellbooks:.*/,
        /wind_spellbooks:.*/,
        /alshanex_familiars:.*/
    ]

    patterns.forEach(pattern => {
        event.modify(pattern, tooltip => {
            if (!tooltip) return
            try {
                tooltip.remove(/Spell Power|Max Mana|Mana Regen|Cooldown|Cast Time|Spell Resist|Spell Slots|Active Spell|Right-Click to cast|When on|Slot:|Imbued|Upgrade|Rarity|School Affinity/)
            } catch (err) {}
        })
    })
})
