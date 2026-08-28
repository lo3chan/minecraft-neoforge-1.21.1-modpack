// KubeJS Server Script - Magic Armor Restriction & Psimetal Exosuit Balance (Clean Jam)
// Maximum allowed armor value for casting: 12.0 (Chainmail tier / Endgame Mage cap)
// Zero armor toughness allowed for spellcasters

function getPlayerArmorStats(player) {
    let armor = 0.0
    let toughness = 0.0

    try {
        armor = player.getAttributeValue('minecraft:generic.armor')
        toughness = player.getAttributeValue('minecraft:generic.armor_toughness')
    } catch (e) {
        try {
            armor = player.armorValue
        } catch (err) {
            armor = 0.0
        }
    }
    return { armor: armor, toughness: toughness }
}

function checkMagicArmorThreshold(event) {
    let player = event.player
    if (!player || player.isCreative()) return

    let stats = getPlayerArmorStats(player)
    let totalArmor = stats.armor
    let totalToughness = stats.toughness

    if (totalArmor > 12.0 || totalToughness > 0) {
        let item = event.item
        let id = '' + item.id

        // Hexcasting: Active Staves, Truncheons, Wands
        if (id.startsWith('hexcasting:staff') || id.startsWith('hexcasting:truncheon') || id.startsWith('hexcasting:wand')) {
            event.cancel()
            player.displayClientMessage(
                Component.literal('§5[Hex] Dense armor blocks the resonance of Media! (Armor: ' + totalArmor + '/12, Toughness: ' + totalToughness + '/0)'),
                true
            )
            player.playSound('hexcasting:casting.cast.fail', 0.8, 1.0)
            return
        }

        // Hexcasting: Stored Spells (Cyphers, Trinkets, Artifacts)
        if (id.startsWith('hexcasting:cypher') || id.startsWith('hexcasting:trinket') || id.startsWith('hexcasting:artifact')) {
            event.cancel()
            player.displayClientMessage(
                Component.literal('§5[Hex] Armor interference jams stored spell activation! (Armor: ' + totalArmor + '/12, Toughness: ' + totalToughness + '/0)'),
                true
            )
            player.playSound('hexcasting:casting.cast.fail', 0.8, 1.0)
            return
        }
    }
}

ItemEvents.rightClicked(event => {
    checkMagicArmorThreshold(event)
})
