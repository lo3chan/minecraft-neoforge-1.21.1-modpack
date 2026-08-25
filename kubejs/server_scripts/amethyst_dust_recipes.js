// Add reliable crushing/crafting recipes for Hex Casting Amethyst Dust
ServerEvents.recipes(event => {
    // 1. Shapeless Crafting: 1 Amethyst Shard -> 4 Amethyst Dust
    event.shapeless('4x hexcasting:amethyst_dust', [
        'minecraft:amethyst_shard'
    ]).id('kubejs:amethyst_shard_to_dust');

    // 2. 1 Amethyst Block -> 16 Amethyst Dust
    event.shapeless('16x hexcasting:amethyst_dust', [
        'minecraft:amethyst_block'
    ]).id('kubejs:amethyst_block_to_dust');

    // 3. Optional mortar / stonecutter style: 1 Shard in Stonecutter -> 4 Dust
    event.stonecutting('4x hexcasting:amethyst_dust', 'minecraft:amethyst_shard').id('kubejs:amethyst_shard_stonecutting');
});

// Also guarantee direct drops on breaking Amethyst Clusters with LootJS/BlockEvents
BlockEvents.broken('minecraft:amethyst_cluster', event => {
    if (!event.player.isCreative()) {
        let count = Math.floor(Math.random() * 4) + 2; // 2 to 5 dust
        event.block.popItem(Item.of('hexcasting:amethyst_dust', count));
    }
});
