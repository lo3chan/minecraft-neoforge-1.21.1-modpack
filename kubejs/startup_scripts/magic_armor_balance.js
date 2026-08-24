// KubeJS Startup Script - Magic Armor Balance
// 1. Psimetal Exosuit -> Leather Tier (7 armor total: Helm 1, Chest 3, Legs 2, Boots 1, Toughness 0)
// 2. Ebony & Ivory Exosuits -> Chainmail Tier (12 armor total: Helm 2, Chest 5, Legs 4, Boots 1, Toughness 0)

ItemEvents.modification(event => {
    // Base Psimetal Exosuit (Leather equivalent)
    event.modify('psi:psimetal_exosuit_helmet', item => {
        item.armorProtection = 1
        item.armorToughness = 0
    })
    event.modify('psi:psimetal_exosuit_chestplate', item => {
        item.armorProtection = 3
        item.armorToughness = 0
    })
    event.modify('psi:psimetal_exosuit_leggings', item => {
        item.armorProtection = 2
        item.armorToughness = 0
    })
    event.modify('psi:psimetal_exosuit_boots', item => {
        item.armorProtection = 1
        item.armorToughness = 0
    })

    // Ebony & Ivory Psimetal Exosuits (Chainmail equivalent)
    ['ebony', 'ivory'].forEach(type => {
        event.modify(psi:_psimetal_exosuit_helmet, item => {
            item.armorProtection = 2
            item.armorToughness = 0
        })
        event.modify(psi:_psimetal_exosuit_chestplate, item => {
            item.armorProtection = 5
            item.armorToughness = 0
        })
        event.modify(psi:_psimetal_exosuit_leggings, item => {
            item.armorProtection = 4
            item.armorToughness = 0
        })
        event.modify(psi:_psimetal_exosuit_boots, item => {
            item.armorProtection = 1
            item.armorToughness = 0
        })
    })
})
