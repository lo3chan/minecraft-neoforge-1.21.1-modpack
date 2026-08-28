// KubeJS Server Script - Master Hex Casting & Spells Realignment Architecture

ServerEvents.recipes(event => {
    // 1. Purge Iron's Spells Workstations, Spellbooks, Inks, Runes, Scrolls & Upgrade Orbs
    event.remove({ output: 'irons_spellbooks:inscription_table' })
    event.remove({ output: 'irons_spellbooks:arcane_anvil' })
    event.remove({ output: 'irons_spellbooks:alchemist_cauldron' })
    event.remove({ output: 'irons_spellbooks:scroll_forge' })
    event.remove({ output: '#irons_spellbooks:spellbooks' })
    event.remove({ output: /irons_spellbooks:netherite_spellbook/})
    event.remove({ output: /irons_spellbooks:.*_ink|/})
    event.remove({ output: /irons_spellbooks:.*_rune/})
    event.remove({ output: /irons_spellbooks:scroll/})
    event.remove({ output: /irons_spellbooks:.*_upgrade_orb/})

    // 2. Purge Wind's Spellbooks Spellbooks, Staffs, Runes & Upgrade Orbs
    event.remove({ output: 'wind_spellbooks:wind_spell_book' })
    event.remove({ output: 'wind_spellbooks:wind_staff' })
    event.remove({ output: 'wind_spellbooks:wind_rune' })
    event.remove({ output: 'wind_spellbooks:scroll_wind' })
    event.remove({ output: 'wind_spellbooks:wind_upgrade_orb' })
    event.remove({ output: /wind_spellbooks:.*_orb/})
})

ServerEvents.chestLootTables(event => {
    // A Overworld Dungeons (Tiers 1-2 Minor Arcana)
    event.modify('minecraft:chests/simple_dungeon', table => {
        table.addPool(pool => {
            pool.rolls = 1
            pool.addItem('minecraft:advancement_fragment').weight(1)
        })
    })
})
