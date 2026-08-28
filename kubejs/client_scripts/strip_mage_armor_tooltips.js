// KubeJS 1.21.1 Client Script - Strip Modded Stats, Spell Power, Mana, and RPG Tooltips
ItemEvents.modifyTooltips(event => {
    event.modify(/.*/, tooltip => {
        if (!tooltip) return
        try {
            // Remove any tooltip line containing spell power, mana, cooldown, or custom RPG attributes
            tooltip.remove(text => {
                if (!text) return false
                let s = text.getString ? text.getString() : ('' + text)
                if (!s) return false
                return (
                    s.includes('Spell Power') ||
                    s.includes('Max Mana') ||
                    s.includes('Mana Regen') ||
                    s.includes('Cooldown') ||
                    s.includes('Cast Time') ||
                    s.includes('Spell Resist') ||
                    s.includes('Spell Slots') ||
                    s.includes('Active Spell') ||
                    s.includes('Right-Click to cast') ||
                    s.includes('Imbued') ||
                    s.includes('School Affinity') ||
                    s.includes('irons_spellbooks.') ||
                    s.includes('attribute.name.irons_spellbooks') ||
                    s.includes('attribute.name.wind_spellbooks') ||
                    s.includes('When on Head:') && s.includes('Spell') ||
                    s.includes('When on Body:') && s.includes('Spell') ||
                    s.includes('When on Legs:') && s.includes('Spell') ||
                    s.includes('When on Feet:') && s.includes('Spell')
                )
            })
        } catch (err) {}
    })
})
