// KubeJS Server Script - Magic Armor Restriction & Psimetal Exosuit Balance (Clean Jam)
// Maximum allowed armor value for casting: 12.0 (Chainmail tier / Endgame Mage cap)
// Zero armor toughness allowed for spellcasters

function checkMagicArmorThreshold(event) {
    let player = event.player
    if (!player || player.isCreative()) return

    let totalArmor = player.getAttributeValue('minecraft:armor')
    let totalToughness = player.getAttributeValue('minecraft:armor_toughness')

    if (totalArmor > 12.0 || totalToughness > 0) {
        let item = event.item
        let id = item.id

        // Psi: CADs, Bullets, Drives
        if (id.startsWith('psi:cad') || id.startsWith('psi:spell_bullet') || id.startsWith('psi:spell_drive')) {
            event.cancel()
            player.displayClientMessage(
                Component.literal('§c[Psi] Heavy armor disrupts psionic resonance! (Armor: ' + totalArmor + '/12, Toughness: ' + totalToughness + '/0)'),
                true
            )
            player.playSound('psi:compile_error', 0.8, 1.0)
            return
        }

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
