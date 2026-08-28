// KubeJS Server Script - Unified Hex & Irons Realignment Architecture
ServerEvents.chestLootTables(event => {
    event.modify('undefined', table => {})
})

ServerEvents.recipes(event => {
    event.remove({ output: 'irons_spellbooks:inscription_table' })
    event.remove({ output: 'irons_spellbooks:arcane_anvil' })
    event.remove({ output: 'irons_spellbooks:alchemist_cauldron' })
    event.remove({ output: 'irons_spellbooks:scroll_forge' })
    event.remove({ output: '#irons_spellbooks:spellbooks' })
    event.remove({ output: /irons_spellbooks:.*_ink/})
    event.remove({ output: /irons_spellbooks:.*_rune/})
    event.remove({ output: /irons_spellbooks:scroll/})
})
