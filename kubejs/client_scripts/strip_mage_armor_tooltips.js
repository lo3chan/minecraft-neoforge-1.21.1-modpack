// KubeJS Client Script - Strip Modded Stats, Spell Power & Tooltips from Mage Robes
ItemEvents.tooltip(event => {
    const mageArmors = [
        /irons_spellbooks:.*_helmet/,
        /irons_spellbooks:.*_chestplate/,
        /irons_spellbooks:.*_leggings/,
        /irons_spellbooks:.*_boots/,
        /irons_spellbooks:.*_hat/,
        /irons_spellbooks:.*_crown/,
        /wind_spellbooks:aeromancer_.*/
    ]

    mageArmors.forEach(pattern => {
        event.add(pattern, (item, advanced, text) => {
            for (let i = text.length - 1; i >= 1; i--) {
                let lineStr = text.get(i).getString()
                if (
                    lineStr.includes('Spell Power') ||
                    lineStr.includes('Max Mana') ||
                    lineStr.includes('Mana Regen') ||
                    lineStr.includes('Cooldown') ||
                    lineStr.includes('Cast Time') ||
                    lineStr.includes('Spell Resist') ||
                    lineStr.includes('When on') ||
                    lineStr.includes('Slot:') ||
                    lineStr.includes('Imbued') ||
                    lineStr.includes('Upgrade') ||
                    lineStr.includes('Rarity')
                ) {
                    text.remove(i)
                }
            }
        })
    })
})
