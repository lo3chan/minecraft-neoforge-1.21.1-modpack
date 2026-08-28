// KubeJS Server Script - Master Hex Casting & Spells Realignment Architecture
// 100% pure Hex Casting interface, Vanilla workstation conversions, High-Tax Media, and Thematic Overcharged Relics

// =========================================================================
// 1. RECIPE & ITEM PURGES (PURGING MODDED WORKSTATIONS, INKS, RUNES & BOOKS)
// =========================================================================
ServerEvents.recipes(event => {
    // 0. Remove broken client-only recipes that cause dedicated server / recipe indexer errors
    event.remove({ type: 'alchemancy:player_head_transmutation' })

    // Purge Iron's Spells Modded Crafting Tables
    event.remove({ output: 'irons_spellbooks:inscription_table' })
    event.remove({ output: 'irons_spellbooks:arcane_anvil' })
    event.remove({ output: 'irons_spellbooks:alchemist_cauldron' })
    event.remove({ output: 'irons_spellbooks:scroll_forge' })

    // Purge Spellbooks, Inks, Runes, Scrolls & Upgrade Orbs
    event.remove({ output: '#irons_spellbooks:spellbooks' })
    event.remove({ output: /irons_spellbooks:.*_spell_book/ })
    event.remove({ output: /irons_spellbooks:netherite_spellbook/ })
    event.remove({ output: /irons_spellbooks:.*_ink/ })
    event.remove({ output: /irons_spellbooks:.*_rune/ })
    event.remove({ output: /irons_spellbooks:scroll/ })
    event.remove({ output: /irons_spellbooks:.*_upgrade_orb/ })

    // Purge Stat-Bloat RPG Curios Rings & Amulets
    event.remove({ output: /irons_spellbooks:.*_ring/ })
    event.remove({ output: /irons_spellbooks:.*_amulet/ })
    event.remove({ output: /irons_spellbooks:.*_talisman/ })

    // Purge Wind's Spellbooks Modded Progression
    event.remove({ output: 'wind_spellbooks:wind_spell_book' })
    event.remove({ output: 'wind_spellbooks:wind_staff' })
    event.remove({ output: 'wind_spellbooks:wind_rune' })
    event.remove({ output: 'wind_spellbooks:wind_upgrade_orb' })
    event.remove({ output: /wind_spellbooks:.*_orb/ })
    event.remove({ output: /wind_spellbooks:.*_ring/ })

    // Fix Undead Revamp smoke bomb recipe with stack size overflow
    event.remove({ id: 'undead_revamp2:smokebombrep' })

    // =========================================================================
    // 2. CRAFTING RECIPES FOR PURE VISUAL MAGE ROBES (<= 12 ARMOR / 0 TOUGHNESS)
    // =========================================================================
    // Pyromancer Robes (Elementalist Tier - 10 Armor)
    event.shaped('irons_spellbooks:pyromancer_helmet', ['WWW', 'WAW', '   '], { W: 'minecraft:red_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:pyromancer_chestplate', ['W W', 'WAW', 'WWW'], { W: 'minecraft:red_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:pyromancer_leggings', ['WWW', 'W W', 'W W'], { W: 'minecraft:red_wool' })
    event.shaped('irons_spellbooks:pyromancer_boots', ['W W', 'A A', '   '], { W: 'minecraft:red_wool', A: 'minecraft:amethyst_shard' })

    // Cryomancer Robes (Elementalist Tier - 10 Armor)
    event.shaped('irons_spellbooks:cryomancer_helmet', ['WWW', 'WAW', '   '], { W: 'minecraft:light_blue_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:cryomancer_chestplate', ['W W', 'WAW', 'WWW'], { W: 'minecraft:light_blue_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:cryomancer_leggings', ['WWW', 'W W', 'W W'], { W: 'minecraft:light_blue_wool' })
    event.shaped('irons_spellbooks:cryomancer_boots', ['W W', 'A A', '   '], { W: 'minecraft:light_blue_wool', A: 'minecraft:amethyst_shard' })

    // Electromancer Robes (Elementalist Tier - 10 Armor)
    event.shaped('irons_spellbooks:electromancer_helmet', ['WWW', 'WAW', '   '], { W: 'minecraft:yellow_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:electromancer_chestplate', ['W W', 'WAW', 'WWW'], { W: 'minecraft:yellow_wool', A: 'minecraft:amethyst_shard' })
    event.shaped('irons_spellbooks:electromancer_leggings', ['WWW', 'W W', 'W W'], { W: 'minecraft:yellow_wool' })
    event.shaped('irons_spellbooks:electromancer_boots', ['W W', 'A A', '   '], { W: 'minecraft:yellow_wool', A: 'minecraft:amethyst_shard' })

    // Archevoker Robes (Endgame Mage Cap - 12 Armor)
    event.shaped('irons_spellbooks:archevoker_helmet', ['WWW', 'GAG', '   '], { W: 'minecraft:purple_wool', G: 'minecraft:gold_ingot', A: 'hexcasting:charged_amethyst' })
    event.shaped('irons_spellbooks:archevoker_chestplate', ['W W', 'GAG', 'WWW'], { W: 'minecraft:purple_wool', G: 'minecraft:gold_ingot', A: 'hexcasting:charged_amethyst' })
    event.shaped('irons_spellbooks:archevoker_leggings', ['WWW', 'G G', 'W W'], { W: 'minecraft:purple_wool', G: 'minecraft:gold_ingot' })
    event.shaped('irons_spellbooks:archevoker_boots', ['W W', 'G G', '   '], { W: 'minecraft:purple_wool', G: 'minecraft:gold_ingot' })

    // Aeromancer Robes (Wind's Spellbooks - 10 Armor)
    event.shaped('wind_spellbooks:aeromancer_helmet', ['WWW', 'FAF', '   '], { W: 'minecraft:white_wool', F: 'minecraft:feather', A: 'minecraft:amethyst_shard' })
    event.shaped('wind_spellbooks:aeromancer_chestplate', ['W W', 'FAF', 'WWW'], { W: 'minecraft:white_wool', F: 'minecraft:feather', A: 'minecraft:amethyst_shard' })
    event.shaped('wind_spellbooks:aeromancer_leggings', ['WWW', 'F F', 'W W'], { W: 'minecraft:white_wool', F: 'minecraft:feather' })
    event.shaped('wind_spellbooks:aeromancer_boots', ['W W', 'F F', '   '], { W: 'minecraft:white_wool', F: 'minecraft:feather' })
})
