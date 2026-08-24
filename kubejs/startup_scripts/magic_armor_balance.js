ItemEvents.modification(event => {
    // Base Psimetal Exosuit (Leather equivalent: 7 total armor, 0 toughness)
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

    // Ebony & Ivory Psimetal Exosuits (Chainmail equivalent: 12 total armor, 0 toughness)
    event.modify('psi:ebony_psimetal_exosuit_helmet', item => {
        item.armorProtection = 2
        item.armorToughness = 0
    })
    event.modify('psi:ebony_psimetal_exosuit_chestplate', item => {
        item.armorProtection = 5
        item.armorToughness = 0
    })
    event.modify('psi:ebony_psimetal_exosuit_leggings', item => {
        item.armorProtection = 4
        item.armorToughness = 0
    })
    event.modify('psi:ebony_psimetal_exosuit_boots', item => {
        item.armorProtection = 1
        item.armorToughness = 0
    })

    event.modify('psi:ivory_psimetal_exosuit_helmet', item => {
        item.armorProtection = 2
        item.armorToughness = 0
    })
    event.modify('psi:ivory_psimetal_exosuit_chestplate', item => {
        item.armorProtection = 5
        item.armorToughness = 0
    })
    event.modify('psi:ivory_psimetal_exosuit_leggings', item => {
        item.armorProtection = 4
        item.armorToughness = 0
    })
    event.modify('psi:ivory_psimetal_exosuit_boots', item => {
        item.armorProtection = 1
        item.armorToughness = 0
    })
})
