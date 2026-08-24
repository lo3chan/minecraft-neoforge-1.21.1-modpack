package com.mcwfences.kikoz.init;

import com.mcwfences.kikoz.objects.FuelItemBlock;
import com.mcwfences.kikoz.util.FuelItemBlockStackable;
import com.mcwfences.kikoz.util.StackableTooltip;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwfences");
   public static final DeferredItem<Item> OAK_PICKET_FENCE = ITEMS.register(
      "oak_picket_fence", () -> new FuelItemBlock((Block)BlockInit.OAK_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PICKET_FENCE = ITEMS.register(
      "spruce_picket_fence", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PICKET_FENCE = ITEMS.register(
      "birch_picket_fence", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PICKET_FENCE = ITEMS.register(
      "jungle_picket_fence", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PICKET_FENCE = ITEMS.register(
      "acacia_picket_fence", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PICKET_FENCE = ITEMS.register(
      "dark_oak_picket_fence", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PICKET_FENCE = ITEMS.register(
      "crimson_picket_fence", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PICKET_FENCE = ITEMS.register(
      "warped_picket_fence", () -> new FuelItemBlock((Block)BlockInit.WARPED_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PICKET_FENCE = ITEMS.register(
      "mangrove_picket_fence", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STOCKADE_FENCE = ITEMS.register(
      "oak_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.OAK_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STOCKADE_FENCE = ITEMS.register(
      "spruce_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STOCKADE_FENCE = ITEMS.register(
      "birch_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.BIRCH_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STOCKADE_FENCE = ITEMS.register(
      "jungle_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STOCKADE_FENCE = ITEMS.register(
      "acacia_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.ACACIA_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STOCKADE_FENCE = ITEMS.register(
      "dark_oak_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STOCKADE_FENCE = ITEMS.register(
      "crimson_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STOCKADE_FENCE = ITEMS.register(
      "warped_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.WARPED_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STOCKADE_FENCE = ITEMS.register(
      "mangrove_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_HORSE_FENCE = ITEMS.register(
      "oak_horse_fence", () -> new FuelItemBlock((Block)BlockInit.OAK_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_HORSE_FENCE = ITEMS.register(
      "spruce_horse_fence", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_HORSE_FENCE = ITEMS.register(
      "birch_horse_fence", () -> new FuelItemBlock((Block)BlockInit.BIRCH_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_HORSE_FENCE = ITEMS.register(
      "jungle_horse_fence", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_HORSE_FENCE = ITEMS.register(
      "acacia_horse_fence", () -> new FuelItemBlock((Block)BlockInit.ACACIA_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_HORSE_FENCE = ITEMS.register(
      "dark_oak_horse_fence", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_HORSE_FENCE = ITEMS.register(
      "crimson_horse_fence", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_HORSE_FENCE = ITEMS.register(
      "warped_horse_fence", () -> new FuelItemBlock((Block)BlockInit.WARPED_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_HORSE_FENCE = ITEMS.register(
      "mangrove_horse_fence", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_WIRED_FENCE = ITEMS.register(
      "oak_wired_fence", () -> new FuelItemBlock((Block)BlockInit.OAK_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WIRED_FENCE = ITEMS.register(
      "spruce_wired_fence", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WIRED_FENCE = ITEMS.register(
      "birch_wired_fence", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WIRED_FENCE = ITEMS.register(
      "jungle_wired_fence", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WIRED_FENCE = ITEMS.register(
      "acacia_wired_fence", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WIRED_FENCE = ITEMS.register(
      "dark_oak_wired_fence", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_WIRED_FENCE = ITEMS.register(
      "crimson_wired_fence", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_WIRED_FENCE = ITEMS.register(
      "warped_wired_fence", () -> new FuelItemBlock((Block)BlockInit.WARPED_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WIRED_FENCE = ITEMS.register(
      "mangrove_wired_fence", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_HEDGE = ITEMS.register("oak_hedge", () -> new FuelItemBlock((Block)BlockInit.OAK_HEDGE.get(), new Properties()));
   public static final DeferredItem<Item> SPRUCE_HEDGE = ITEMS.register(
      "spruce_hedge", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_HEDGE = ITEMS.register(
      "birch_hedge", () -> new FuelItemBlock((Block)BlockInit.BIRCH_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_HEDGE = ITEMS.register(
      "jungle_hedge", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_HEDGE = ITEMS.register(
      "acacia_hedge", () -> new FuelItemBlock((Block)BlockInit.ACACIA_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_HEDGE = ITEMS.register(
      "dark_oak_hedge", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_HEDGE = ITEMS.register(
      "mangrove_hedge", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> AZALEA_HEDGE = ITEMS.register(
      "azalea_hedge", () -> new FuelItemBlock((Block)BlockInit.AZALEA_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> FLOWERING_AZALEA_HEDGE = ITEMS.register(
      "flowering_azalea_hedge", () -> new FuelItemBlock((Block)BlockInit.FLOWERING_AZALEA_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_FENCE = ITEMS.register(
      "bamboo_fence", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_HIGHLEY_GATE = ITEMS.register(
      "oak_highley_gate", () -> new FuelItemBlock((Block)BlockInit.OAK_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_HIGHLEY_GATE = ITEMS.register(
      "spruce_highley_gate", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_HIGHLEY_GATE = ITEMS.register(
      "birch_highley_gate", () -> new FuelItemBlock((Block)BlockInit.BIRCH_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_HIGHLEY_GATE = ITEMS.register(
      "jungle_highley_gate", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_HIGHLEY_GATE = ITEMS.register(
      "acacia_highley_gate", () -> new FuelItemBlock((Block)BlockInit.ACACIA_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_HIGHLEY_GATE = ITEMS.register(
      "dark_oak_highley_gate", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_HIGHLEY_GATE = ITEMS.register(
      "crimson_highley_gate", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_HIGHLEY_GATE = ITEMS.register(
      "warped_highley_gate", () -> new FuelItemBlock((Block)BlockInit.WARPED_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_HIGHLEY_GATE = ITEMS.register(
      "mangrove_highley_gate", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PYRAMID_GATE = ITEMS.register(
      "oak_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.OAK_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PYRAMID_GATE = ITEMS.register(
      "spruce_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PYRAMID_GATE = ITEMS.register(
      "birch_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PYRAMID_GATE = ITEMS.register(
      "jungle_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PYRAMID_GATE = ITEMS.register(
      "acacia_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PYRAMID_GATE = ITEMS.register(
      "dark_oak_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PYRAMID_GATE = ITEMS.register(
      "crimson_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PYRAMID_GATE = ITEMS.register(
      "warped_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.WARPED_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PYRAMID_GATE = ITEMS.register(
      "mangrove_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_STONE_BRICK_WALL = ITEMS.register(
      "modern_stone_brick_wall", () -> new BlockItem((Block)BlockInit.MODERN_STONE_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_ANDESITE_WALL = ITEMS.register(
      "modern_andesite_wall", () -> new BlockItem((Block)BlockInit.MODERN_ANDESITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_DIORITE_WALL = ITEMS.register(
      "modern_diorite_wall", () -> new BlockItem((Block)BlockInit.MODERN_DIORITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_GRANITE_WALL = ITEMS.register(
      "modern_granite_wall", () -> new BlockItem((Block)BlockInit.MODERN_GRANITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_SANDSTONE_WALL = ITEMS.register(
      "modern_sandstone_wall", () -> new BlockItem((Block)BlockInit.MODERN_SANDSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_RED_SANDSTONE_WALL = ITEMS.register(
      "modern_red_sandstone_wall", () -> new BlockItem((Block)BlockInit.MODERN_RED_SANDSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_BLACKSTONE_WALL = ITEMS.register(
      "modern_blackstone_wall", () -> new BlockItem((Block)BlockInit.MODERN_BLACKSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_NETHER_BRICK_WALL = ITEMS.register(
      "modern_nether_brick_wall", () -> new BlockItem((Block)BlockInit.MODERN_NETHER_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_END_BRICK_WALL = ITEMS.register(
      "modern_end_brick_wall", () -> new BlockItem((Block)BlockInit.MODERN_END_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_DEEPSLATE_WALL = ITEMS.register(
      "modern_deepslate_wall", () -> new BlockItem((Block)BlockInit.MODERN_DEEPSLATE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_DEEPSLATE_BRICK_WALL = ITEMS.register(
      "modern_deepslate_brick_wall", () -> new BlockItem((Block)BlockInit.MODERN_DEEPSLATE_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_QUARTZ_WALL = ITEMS.register(
      "modern_quartz_wall", () -> new BlockItem((Block)BlockInit.MODERN_QUARTZ_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_MUD_BRICK_WALL = ITEMS.register(
      "modern_mud_brick_wall", () -> new BlockItem((Block)BlockInit.MODERN_MUD_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MODERN_PRISMARINE_WALL = ITEMS.register(
      "modern_prismarine_wall", () -> new BlockItem((Block)BlockInit.MODERN_PRISMARINE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_STONE_BRICK_WALL = ITEMS.register(
      "railing_stone_brick_wall", () -> new BlockItem((Block)BlockInit.RAILING_STONE_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_ANDESITE_WALL = ITEMS.register(
      "railing_andesite_wall", () -> new BlockItem((Block)BlockInit.RAILING_ANDESITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_DIORITE_WALL = ITEMS.register(
      "railing_diorite_wall", () -> new BlockItem((Block)BlockInit.RAILING_DIORITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_GRANITE_WALL = ITEMS.register(
      "railing_granite_wall", () -> new BlockItem((Block)BlockInit.RAILING_GRANITE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_SANDSTONE_WALL = ITEMS.register(
      "railing_sandstone_wall", () -> new BlockItem((Block)BlockInit.RAILING_SANDSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_RED_SANDSTONE_WALL = ITEMS.register(
      "railing_red_sandstone_wall", () -> new BlockItem((Block)BlockInit.RAILING_RED_SANDSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_BLACKSTONE_WALL = ITEMS.register(
      "railing_blackstone_wall", () -> new BlockItem((Block)BlockInit.RAILING_BLACKSTONE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_NETHER_BRICK_WALL = ITEMS.register(
      "railing_nether_brick_wall", () -> new BlockItem((Block)BlockInit.RAILING_NETHER_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_END_BRICK_WALL = ITEMS.register(
      "railing_end_brick_wall", () -> new BlockItem((Block)BlockInit.RAILING_END_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_DEEPSLATE_WALL = ITEMS.register(
      "railing_deepslate_wall", () -> new BlockItem((Block)BlockInit.RAILING_DEEPSLATE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_DEEPSLATE_BRICK_WALL = ITEMS.register(
      "railing_deepslate_brick_wall", () -> new BlockItem((Block)BlockInit.RAILING_DEEPSLATE_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_QUARTZ_WALL = ITEMS.register(
      "railing_quartz_wall", () -> new BlockItem((Block)BlockInit.RAILING_QUARTZ_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_MUD_BRICK_WALL = ITEMS.register(
      "railing_mud_brick_wall", () -> new BlockItem((Block)BlockInit.RAILING_MUD_BRICK_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RAILING_PRISMARINE_WALL = ITEMS.register(
      "railing_prismarine_wall", () -> new BlockItem((Block)BlockInit.RAILING_PRISMARINE_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICK_RAILING_GATE = ITEMS.register(
      "stone_brick_railing_gate", () -> new BlockItem((Block)BlockInit.STONE_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_RAILING_GATE = ITEMS.register(
      "andesite_railing_gate", () -> new BlockItem((Block)BlockInit.ANDESITE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_RAILING_GATE = ITEMS.register(
      "diorite_railing_gate", () -> new BlockItem((Block)BlockInit.DIORITE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_RAILING_GATE = ITEMS.register(
      "granite_railing_gate", () -> new BlockItem((Block)BlockInit.GRANITE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_RAILING_GATE = ITEMS.register(
      "sandstone_railing_gate", () -> new BlockItem((Block)BlockInit.SANDSTONE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_RAILING_GATE = ITEMS.register(
      "red_sandstone_railing_gate", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_RAILING_GATE = ITEMS.register(
      "blackstone_railing_gate", () -> new BlockItem((Block)BlockInit.BLACKSTONE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRICK_RAILING_GATE = ITEMS.register(
      "blackstone_brick_railing_gate", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICK_RAILING_GATE = ITEMS.register(
      "nether_brick_railing_gate", () -> new BlockItem((Block)BlockInit.NETHER_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> END_BRICK_RAILING_GATE = ITEMS.register(
      "end_brick_railing_gate", () -> new BlockItem((Block)BlockInit.END_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_RAILING_GATE = ITEMS.register(
      "deepslate_railing_gate", () -> new BlockItem((Block)BlockInit.DEEPSLATE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_RAILING_GATE = ITEMS.register(
      "deepslate_brick_railing_gate", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_RAILING_GATE = ITEMS.register(
      "quartz_railing_gate", () -> new BlockItem((Block)BlockInit.QUARTZ_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_RAILING_GATE = ITEMS.register(
      "mud_brick_railing_gate", () -> new BlockItem((Block)BlockInit.MUD_BRICK_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_RAILING_GATE = ITEMS.register(
      "prismarine_railing_gate", () -> new BlockItem((Block)BlockInit.PRISMARINE_RAILING_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PICKET_FENCE = ITEMS.register(
      "cherry_picket_fence", () -> new FuelItemBlock((Block)BlockInit.CHERRY_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STOCKADE_FENCE = ITEMS.register(
      "cherry_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.CHERRY_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_HORSE_FENCE = ITEMS.register(
      "cherry_horse_fence", () -> new FuelItemBlock((Block)BlockInit.CHERRY_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WIRED_FENCE = ITEMS.register(
      "cherry_wired_fence", () -> new FuelItemBlock((Block)BlockInit.CHERRY_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_HEDGE = ITEMS.register(
      "cherry_hedge", () -> new FuelItemBlock((Block)BlockInit.CHERRY_HEDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_HIGHLEY_GATE = ITEMS.register(
      "cherry_highley_gate", () -> new FuelItemBlock((Block)BlockInit.CHERRY_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PYRAMID_GATE = ITEMS.register(
      "cherry_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.CHERRY_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PICKET_FENCE = ITEMS.register(
      "bamboo_picket_fence", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_PICKET_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_STOCKADE_FENCE = ITEMS.register(
      "bamboo_stockade_fence", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_STOCKADE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_HORSE_FENCE = ITEMS.register(
      "bamboo_horse_fence", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_HORSE_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_WIRED_FENCE = ITEMS.register(
      "bamboo_wired_fence", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_WIRED_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_HIGHLEY_GATE = ITEMS.register(
      "bamboo_highley_gate", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_HIGHLEY_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PYRAMID_GATE = ITEMS.register(
      "bamboo_pyramid_gate", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_PYRAMID_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_PILLAR_WALL = ITEMS.register(
      "stone_pillar_wall", () -> new BlockItem((Block)BlockInit.STONE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_PILLAR_WALL = ITEMS.register(
      "andesite_pillar_wall", () -> new BlockItem((Block)BlockInit.ANDESITE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_PILLAR_WALL = ITEMS.register(
      "diorite_pillar_wall", () -> new BlockItem((Block)BlockInit.DIORITE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_PILLAR_WALL = ITEMS.register(
      "granite_pillar_wall", () -> new BlockItem((Block)BlockInit.GRANITE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_PILLAR_WALL = ITEMS.register(
      "sandstone_pillar_wall", () -> new BlockItem((Block)BlockInit.SANDSTONE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_PILLAR_WALL = ITEMS.register(
      "red_sandstone_pillar_wall", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_PILLAR_WALL = ITEMS.register(
      "blackstone_pillar_wall", () -> new BlockItem((Block)BlockInit.BLACKSTONE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICK_PILLAR_WALL = ITEMS.register(
      "nether_brick_pillar_wall", () -> new BlockItem((Block)BlockInit.NETHER_BRICK_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> END_BRICK_PILLAR_WALL = ITEMS.register(
      "end_brick_pillar_wall", () -> new BlockItem((Block)BlockInit.END_BRICK_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_PILLAR_WALL = ITEMS.register(
      "deepslate_pillar_wall", () -> new BlockItem((Block)BlockInit.DEEPSLATE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_PILLAR_WALL = ITEMS.register(
      "deepslate_brick_pillar_wall", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BRICK_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_PILLAR_WALL = ITEMS.register(
      "quartz_pillar_wall", () -> new BlockItem((Block)BlockInit.QUARTZ_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_PILLAR_WALL = ITEMS.register(
      "mud_brick_pillar_wall", () -> new BlockItem((Block)BlockInit.MUD_BRICK_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_PILLAR_WALL = ITEMS.register(
      "prismarine_pillar_wall", () -> new BlockItem((Block)BlockInit.PRISMARINE_PILLAR_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_GRASS_TOPPED_WALL = ITEMS.register(
      "stone_grass_topped_wall", () -> new BlockItem((Block)BlockInit.STONE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_GRASS_TOPPED_WALL = ITEMS.register(
      "andesite_grass_topped_wall", () -> new BlockItem((Block)BlockInit.ANDESITE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_GRASS_TOPPED_WALL = ITEMS.register(
      "diorite_grass_topped_wall", () -> new BlockItem((Block)BlockInit.DIORITE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_GRASS_TOPPED_WALL = ITEMS.register(
      "granite_grass_topped_wall", () -> new BlockItem((Block)BlockInit.GRANITE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_GRASS_TOPPED_WALL = ITEMS.register(
      "sandstone_grass_topped_wall", () -> new BlockItem((Block)BlockInit.SANDSTONE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_GRASS_TOPPED_WALL = ITEMS.register(
      "red_sandstone_grass_topped_wall", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_GRASS_TOPPED_WALL = ITEMS.register(
      "blackstone_grass_topped_wall", () -> new BlockItem((Block)BlockInit.BLACKSTONE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICK_GRASS_TOPPED_WALL = ITEMS.register(
      "nether_brick_grass_topped_wall", () -> new BlockItem((Block)BlockInit.NETHER_BRICK_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> END_BRICK_GRASS_TOPPED_WALL = ITEMS.register(
      "end_brick_grass_topped_wall", () -> new BlockItem((Block)BlockInit.END_BRICK_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_GRASS_TOPPED_WALL = ITEMS.register(
      "deepslate_grass_topped_wall", () -> new BlockItem((Block)BlockInit.DEEPSLATE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_GRASS_TOPPED_WALL = ITEMS.register(
      "deepslate_brick_grass_topped_wall", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BRICK_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_GRASS_TOPPED_WALL = ITEMS.register(
      "quartz_grass_topped_wall", () -> new BlockItem((Block)BlockInit.QUARTZ_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_GRASS_TOPPED_WALL = ITEMS.register(
      "mud_brick_grass_topped_wall", () -> new BlockItem((Block)BlockInit.MUD_BRICK_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_GRASS_TOPPED_WALL = ITEMS.register(
      "prismarine_grass_topped_wall", () -> new BlockItem((Block)BlockInit.PRISMARINE_GRASS_TOPPED_WALL.get(), new Properties())
   );
   public static final DeferredItem<Item> WOODEN_CHEVAL_DE_FRISE = ITEMS.register(
      "wooden_cheval_de_frise", () -> new FuelItemBlock((Block)BlockInit.WOODEN_CHEVAL_DE_FRISE.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_CHEVAL_DE_FRISE = ITEMS.register(
      "iron_cheval_de_frise", () -> new BlockItem((Block)BlockInit.IRON_CHEVAL_DE_FRISE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACORN_METAL_FENCE = ITEMS.register(
      "acorn_metal_fence", () -> new StackableTooltip((Block)BlockInit.ACORN_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> PANELLED_METAL_FENCE = ITEMS.register(
      "panelled_metal_fence", () -> new StackableTooltip((Block)BlockInit.PANELLED_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MESH_METAL_FENCE = ITEMS.register(
      "mesh_metal_fence", () -> new StackableTooltip((Block)BlockInit.MESH_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> ORNATE_METAL_FENCE = ITEMS.register(
      "ornate_metal_fence", () -> new StackableTooltip((Block)BlockInit.ORNATE_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUARDIAN_METAL_FENCE = ITEMS.register(
      "guardian_metal_fence", () -> new StackableTooltip((Block)BlockInit.GUARDIAN_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> GOTHIC_METAL_FENCE = ITEMS.register(
      "gothic_metal_fence", () -> new StackableTooltip((Block)BlockInit.GOTHIC_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> VINTAGE_METAL_FENCE = ITEMS.register(
      "vintage_metal_fence", () -> new StackableTooltip((Block)BlockInit.VINTAGE_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CURVED_METAL_FENCE = ITEMS.register(
      "curved_metal_fence", () -> new StackableTooltip((Block)BlockInit.CURVED_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> TWILIGHT_METAL_FENCE = ITEMS.register(
      "twilight_metal_fence", () -> new StackableTooltip((Block)BlockInit.TWILIGHT_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> EXPANDED_MESH_METAL_FENCE = ITEMS.register(
      "expanded_mesh_metal_fence", () -> new StackableTooltip((Block)BlockInit.EXPANDED_MESH_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> FORTRESS_METAL_FENCE = ITEMS.register(
      "fortress_metal_fence", () -> new StackableTooltip((Block)BlockInit.FORTRESS_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> BASTION_METAL_FENCE = ITEMS.register(
      "bastion_metal_fence", () -> new StackableTooltip((Block)BlockInit.BASTION_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> DOUBLE_CURVED_METAL_FENCE = ITEMS.register(
      "double_curved_metal_fence", () -> new StackableTooltip((Block)BlockInit.DOUBLE_CURVED_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> CATHEDRAL_METAL_FENCE = ITEMS.register(
      "cathedral_metal_fence", () -> new StackableTooltip((Block)BlockInit.CATHEDRAL_METAL_FENCE.get(), new Properties())
   );
   public static final DeferredItem<Item> MAJESTIC_METAL_FENCE_GATE = ITEMS.register(
      "majestic_metal_fence_gate", () -> new StackableTooltip((Block)BlockInit.MAJESTIC_METAL_FENCE_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CURVED_METAL_FENCE_GATE = ITEMS.register(
      "curved_metal_fence_gate", () -> new StackableTooltip((Block)BlockInit.CURVED_METAL_FENCE_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MESH_METAL_FENCE_GATE = ITEMS.register(
      "mesh_metal_fence_gate", () -> new StackableTooltip((Block)BlockInit.MESH_METAL_FENCE_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> PANELLED_METAL_FENCE_GATE = ITEMS.register(
      "panelled_metal_fence_gate", () -> new StackableTooltip((Block)BlockInit.PANELLED_METAL_FENCE_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BASTION_METAL_FENCE_GATE = ITEMS.register(
      "bastion_metal_fence_gate", () -> new StackableTooltip((Block)BlockInit.BASTION_METAL_FENCE_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_CURVED_GATE = ITEMS.register(
      "oak_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.OAK_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CURVED_GATE = ITEMS.register(
      "spruce_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.SPRUCE_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CURVED_GATE = ITEMS.register(
      "birch_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.BIRCH_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CURVED_GATE = ITEMS.register(
      "jungle_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.JUNGLE_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CURVED_GATE = ITEMS.register(
      "acacia_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.ACACIA_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CURVED_GATE = ITEMS.register(
      "dark_oak_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.DARK_OAK_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CURVED_GATE = ITEMS.register(
      "crimson_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.CRIMSON_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CURVED_GATE = ITEMS.register(
      "warped_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.WARPED_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CURVED_GATE = ITEMS.register(
      "mangrove_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.MANGROVE_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CURVED_GATE = ITEMS.register(
      "cherry_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.CHERRY_CURVED_GATE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_CURVED_GATE = ITEMS.register(
      "bamboo_curved_gate", () -> new FuelItemBlockStackable((Block)BlockInit.BAMBOO_CURVED_GATE.get(), new Properties())
   );
}
