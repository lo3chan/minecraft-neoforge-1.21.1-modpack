package com.mcwbridges.kikoz.init;

import com.mcwbridges.kikoz.objects.items.Plier;
import com.mcwbridges.kikoz.util.BlockItemWithInfo;
import com.mcwbridges.kikoz.util.FuelBlockItemWithInfo;
import com.mcwbridges.kikoz.util.FuelItemBlock;
import com.mcwbridges.kikoz.util.LightInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwbridges");
   public static final DeferredItem<Item> PLIERS = ITEMS.register("pliers", () -> new Plier(new Properties()));
   public static final DeferredItem<Item> IRON_BRIDGE = ITEMS.register(
      "iron_bridge", () -> new BlockItemWithInfo((Block)BlockInit.IRON_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "oak_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.OAK_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "birch_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.BIRCH_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "acacia_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.ACACIA_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "spruce_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.SPRUCE_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "jungle_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.JUNGLE_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "dark_oak_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.DARK_OAK_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "crimson_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.CRIMSON_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "warped_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.WARPED_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "mangrove_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.MANGROVE_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_OAK_BRIDGE = ITEMS.register(
      "rope_oak_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_OAK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_BIRCH_BRIDGE = ITEMS.register(
      "rope_birch_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_BIRCH_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_SPRUCE_BRIDGE = ITEMS.register(
      "rope_spruce_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_SPRUCE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_JUNGLE_BRIDGE = ITEMS.register(
      "rope_jungle_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_JUNGLE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_ACACIA_BRIDGE = ITEMS.register(
      "rope_acacia_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_ACACIA_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_DARK_OAK_BRIDGE = ITEMS.register(
      "rope_dark_oak_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_DARK_OAK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_CRIMSON_BRIDGE = ITEMS.register(
      "rope_crimson_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_CRIMSON_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_WARPED_BRIDGE = ITEMS.register(
      "rope_warped_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_WARPED_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_MANGROVE_BRIDGE = ITEMS.register(
      "rope_mangrove_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_MANGROVE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_BRIDGE = ITEMS.register(
      "brick_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BRICK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_BRIDGE = ITEMS.register(
      "sandstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.SANDSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICK_BRIDGE = ITEMS.register(
      "stone_brick_bridge", () -> new BlockItemWithInfo((Block)BlockInit.STONE_BRICK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_SANDSTONE_BRIDGE = ITEMS.register(
      "orange_sandstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.ORANGE_SANDSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRIDGE = ITEMS.register(
      "blackstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BLACKSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_BRICK_BRIDGE = ITEMS.register(
      "mossy_stone_brick_bridge", () -> new BlockItemWithInfo((Block)BlockInit.MOSSY_STONE_BRICK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_BRIDGE = ITEMS.register(
      "deepslate_brick_bridge", () -> new BlockItemWithInfo((Block)BlockInit.DEEPSLATE_BRICK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TILE_BRIDGE = ITEMS.register(
      "deepslate_tile_bridge", () -> new BlockItemWithInfo((Block)BlockInit.DEEPSLATE_TILE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_BRIDGE = ITEMS.register(
      "mud_brick_bridge", () -> new BlockItemWithInfo((Block)BlockInit.MUD_BRICK_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_RAIL_BRIDGE = ITEMS.register(
      "oak_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.OAK_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_RAIL_BRIDGE = ITEMS.register(
      "spruce_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_RAIL_BRIDGE = ITEMS.register(
      "birch_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.BIRCH_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_RAIL_BRIDGE = ITEMS.register(
      "jungle_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_RAIL_BRIDGE = ITEMS.register(
      "acacia_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.ACACIA_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_RAIL_BRIDGE = ITEMS.register(
      "dark_oak_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_RAIL_BRIDGE = ITEMS.register(
      "crimson_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_RAIL_BRIDGE = ITEMS.register(
      "warped_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.WARPED_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_RAIL_BRIDGE = ITEMS.register(
      "mangrove_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BRIDGE = ITEMS.register(
      "bamboo_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.BAMBOO_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DRY_BAMBOO_BRIDGE = ITEMS.register(
      "dry_bamboo_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.DRY_BAMBOO_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_BRIDGE_PIER = ITEMS.register(
      "iron_bridge_pier", () -> new BlockItem((Block)BlockInit.IRON_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BRIDGE_PIER = ITEMS.register(
      "oak_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.OAK_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BRIDGE_PIER = ITEMS.register(
      "spruce_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BRIDGE_PIER = ITEMS.register(
      "birch_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BRIDGE_PIER = ITEMS.register(
      "jungle_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BRIDGE_PIER = ITEMS.register(
      "acacia_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BRIDGE_PIER = ITEMS.register(
      "dark_oak_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BRIDGE_PIER = ITEMS.register(
      "crimson_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BRIDGE_PIER = ITEMS.register(
      "warped_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.WARPED_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BRIDGE_PIER = ITEMS.register(
      "mangrove_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_BRIDGE_PIER = ITEMS.register(
      "brick_bridge_pier", () -> new BlockItem((Block)BlockInit.BRICK_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_BRIDGE_PIER = ITEMS.register(
      "sandstone_bridge_pier", () -> new BlockItem((Block)BlockInit.SANDSTONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRIDGE_PIER = ITEMS.register(
      "stone_bridge_pier", () -> new BlockItem((Block)BlockInit.STONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_BRIDGE_PIER = ITEMS.register(
      "red_sandstone_bridge_pier", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRIDGE_PIER = ITEMS.register(
      "blackstone_bridge_pier", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_BRIDGE_PIER = ITEMS.register(
      "mossy_stone_bridge_pier", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_BRIDGE_PIER = ITEMS.register(
      "deepslate_brick_bridge_pier", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BRICK_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TILE_BRIDGE_PIER = ITEMS.register(
      "deepslate_tile_bridge_pier", () -> new BlockItem((Block)BlockInit.DEEPSLATE_TILE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_BRIDGE_PIER = ITEMS.register(
      "mud_brick_bridge_pier", () -> new BlockItem((Block)BlockInit.MUD_BRICK_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BRIDGE_PIER = ITEMS.register(
      "bamboo_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> DRY_BAMBOO_BRIDGE_PIER = ITEMS.register(
      "dry_bamboo_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.DRY_BAMBOO_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_BRIDGE_STAIR = ITEMS.register(
      "iron_bridge_stair", () -> new BlockItem((Block)BlockInit.IRON_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOG_BRIDGE_STAIR = ITEMS.register(
      "oak_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.OAK_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOG_BRIDGE_STAIR = ITEMS.register(
      "spruce_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOG_BRIDGE_STAIR = ITEMS.register(
      "birch_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.BIRCH_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOG_BRIDGE_STAIR = ITEMS.register(
      "jungle_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOG_BRIDGE_STAIR = ITEMS.register(
      "acacia_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.ACACIA_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOG_BRIDGE_STAIR = ITEMS.register(
      "dark_oak_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOG_BRIDGE_STAIR = ITEMS.register(
      "crimson_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOG_BRIDGE_STAIR = ITEMS.register(
      "warped_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.WARPED_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOG_BRIDGE_STAIR = ITEMS.register(
      "mangrove_log_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_ROPE_BRIDGE_STAIR = ITEMS.register(
      "oak_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.OAK_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_ROPE_BRIDGE_STAIR = ITEMS.register(
      "spruce_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_ROPE_BRIDGE_STAIR = ITEMS.register(
      "birch_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.BIRCH_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_ROPE_BRIDGE_STAIR = ITEMS.register(
      "jungle_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_ROPE_BRIDGE_STAIR = ITEMS.register(
      "acacia_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.ACACIA_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_ROPE_BRIDGE_STAIR = ITEMS.register(
      "dark_oak_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_ROPE_BRIDGE_STAIR = ITEMS.register(
      "crimson_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_ROPE_BRIDGE_STAIR = ITEMS.register(
      "warped_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.WARPED_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_ROPE_BRIDGE_STAIR = ITEMS.register(
      "mangrove_rope_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICK_BRIDGE_STAIR = ITEMS.register(
      "stone_brick_bridge_stair", () -> new BlockItem((Block)BlockInit.STONE_BRICK_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_BRIDGE_STAIR = ITEMS.register(
      "sandstone_bridge_stair", () -> new BlockItem((Block)BlockInit.SANDSTONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_BRIDGE_STAIR = ITEMS.register(
      "brick_bridge_stair", () -> new BlockItem((Block)BlockInit.BRICK_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_BRIDGE_STAIR = ITEMS.register(
      "red_sandstone_bridge_stair", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRIDGE_STAIR = ITEMS.register(
      "blackstone_bridge_stair", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_BRIDGE_STAIR = ITEMS.register(
      "mossy_stone_bridge_stair", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BRICK_BRIDGE_STAIR = ITEMS.register(
      "deepslate_brick_bridge_stair", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BRICK_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TILE_BRIDGE_STAIR = ITEMS.register(
      "deepslate_tile_bridge_stair", () -> new BlockItem((Block)BlockInit.DEEPSLATE_TILE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_BRIDGE_STAIR = ITEMS.register(
      "mud_brick_bridge_stair", () -> new BlockItem((Block)BlockInit.MUD_BRICK_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BRIDGE_STAIR = ITEMS.register(
      "bamboo_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DRY_BAMBOO_BRIDGE_STAIR = ITEMS.register(
      "dry_bamboo_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.DRY_BAMBOO_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOG_BRIDGE_MIDDLE = ITEMS.register(
      "cherry_log_bridge_middle", () -> new FuelBlockItemWithInfo((Block)BlockInit.CHERRY_LOG_BRIDGE_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> ROPE_CHERRY_BRIDGE = ITEMS.register(
      "rope_cherry_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ROPE_CHERRY_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_RAIL_BRIDGE = ITEMS.register(
      "cherry_rail_bridge", () -> new FuelItemBlock((Block)BlockInit.CHERRY_RAIL_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BRIDGE_PIER = ITEMS.register(
      "cherry_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOG_BRIDGE_STAIR = ITEMS.register(
      "cherry_log_bridge_stair", () -> new FuelBlockItemWithInfo((Block)BlockInit.CHERRY_LOG_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_ROPE_BRIDGE_STAIR = ITEMS.register(
      "cherry_rope_bridge_stair", () -> new FuelBlockItemWithInfo((Block)BlockInit.CHERRY_ROPE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BRIDGE_TORCH = ITEMS.register(
      "bridge_torch", () -> new LightInfo((Block)BlockInit.BRIDGE_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRIDGE_LANTERN = ITEMS.register(
      "bridge_lantern", () -> new LightInfo((Block)BlockInit.BRIDGE_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> ASIAN_RED_BRIDGE = ITEMS.register(
      "asian_red_bridge", () -> new FuelBlockItemWithInfo((Block)BlockInit.ASIAN_RED_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> GLASS_BRIDGE = ITEMS.register(
      "glass_bridge", () -> new BlockItemWithInfo((Block)BlockInit.GLASS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_BRIDGE = ITEMS.register(
      "cobblestone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.COBBLESTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_BRIDGE = ITEMS.register(
      "mossy_cobblestone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.MOSSY_COBBLESTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_BRIDGE = ITEMS.register(
      "andesite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.ANDESITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_BRIDGE = ITEMS.register(
      "granite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.GRANITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_BRIDGE = ITEMS.register(
      "diorite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.DIORITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICKS_BRIDGE = ITEMS.register(
      "prismarine_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.PRISMARINE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_BRIDGE = ITEMS.register(
      "nether_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.NETHER_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> END_STONE_BRICKS_BRIDGE = ITEMS.register(
      "end_stone_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.END_STONE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_BRIDGE_PIER = ITEMS.register(
      "cobblestone_bridge_pier", () -> new BlockItem((Block)BlockInit.COBBLESTONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_BRIDGE_PIER = ITEMS.register(
      "mossy_cobblestone_bridge_pier", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_BRIDGE_PIER = ITEMS.register(
      "andesite_bridge_pier", () -> new BlockItem((Block)BlockInit.ANDESITE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_BRIDGE_PIER = ITEMS.register(
      "granite_bridge_pier", () -> new BlockItem((Block)BlockInit.GRANITE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_BRIDGE_PIER = ITEMS.register(
      "diorite_bridge_pier", () -> new BlockItem((Block)BlockInit.DIORITE_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICKS_BRIDGE_PIER = ITEMS.register(
      "prismarine_bricks_bridge_pier", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICKS_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_BRIDGE_PIER = ITEMS.register(
      "nether_bricks_bridge_pier", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> END_STONE_BRICKS_BRIDGE_PIER = ITEMS.register(
      "end_stone_bricks_bridge_pier", () -> new BlockItem((Block)BlockInit.END_STONE_BRICKS_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_BRIDGE_STAIR = ITEMS.register(
      "cobblestone_bridge_stair", () -> new BlockItem((Block)BlockInit.COBBLESTONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_BRIDGE_STAIR = ITEMS.register(
      "mossy_cobblestone_bridge_stair", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_BRIDGE_STAIR = ITEMS.register(
      "andesite_bridge_stair", () -> new BlockItem((Block)BlockInit.ANDESITE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_BRIDGE_STAIR = ITEMS.register(
      "granite_bridge_stair", () -> new BlockItem((Block)BlockInit.GRANITE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_BRIDGE_STAIR = ITEMS.register(
      "diorite_bridge_stair", () -> new BlockItem((Block)BlockInit.DIORITE_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICKS_BRIDGE_STAIR = ITEMS.register(
      "prismarine_bricks_bridge_stair", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICKS_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_BRIDGE_STAIR = ITEMS.register(
      "nether_bricks_bridge_stair", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> END_STONE_BRICKS_BRIDGE_STAIR = ITEMS.register(
      "end_stone_bricks_bridge_stair", () -> new BlockItem((Block)BlockInit.END_STONE_BRICKS_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_STONE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_stone_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_STONE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_SANDSTONE_BRIDGE = ITEMS.register(
      "balustrade_sandstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_SANDSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_ORANGE_SANDSTONE_BRIDGE = ITEMS.register(
      "balustrade_orange_sandstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_ORANGE_SANDSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_BLACKSTONE_BRIDGE = ITEMS.register(
      "balustrade_blackstone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_BLACKSTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_MOSSY_STONE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_mossy_stone_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_MOSSY_STONE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_DEEPSLATE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_deepslate_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_DEEPSLATE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_DEEPSLATE_TILES_BRIDGE = ITEMS.register(
      "balustrade_deepslate_tiles_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_DEEPSLATE_TILES_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_MUD_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_mud_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_MUD_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_COBBLESTONE_BRIDGE = ITEMS.register(
      "balustrade_cobblestone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_COBBLESTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_MOSSY_COBBLESTONE_BRIDGE = ITEMS.register(
      "balustrade_mossy_cobblestone_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_MOSSY_COBBLESTONE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_ANDESITE_BRIDGE = ITEMS.register(
      "balustrade_andesite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_ANDESITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_GRANITE_BRIDGE = ITEMS.register(
      "balustrade_granite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_GRANITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_DIORITE_BRIDGE = ITEMS.register(
      "balustrade_diorite_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_DIORITE_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_PRISMARINE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_prismarine_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_PRISMARINE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_NETHER_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_nether_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_NETHER_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> BALUSTRADE_END_STONE_BRICKS_BRIDGE = ITEMS.register(
      "balustrade_end_stone_bricks_bridge", () -> new BlockItemWithInfo((Block)BlockInit.BALUSTRADE_END_STONE_BRICKS_BRIDGE.get(), new Properties())
   );
   public static final DeferredItem<Item> ASIAN_RED_BRIDGE_PIER = ITEMS.register(
      "asian_red_bridge_pier", () -> new FuelItemBlock((Block)BlockInit.ASIAN_RED_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> GLASS_BRIDGE_PIER = ITEMS.register(
      "glass_bridge_pier", () -> new BlockItem((Block)BlockInit.GLASS_BRIDGE_PIER.get(), new Properties())
   );
   public static final DeferredItem<Item> ASIAN_RED_BRIDGE_STAIR = ITEMS.register(
      "asian_red_bridge_stair", () -> new FuelItemBlock((Block)BlockInit.ASIAN_RED_BRIDGE_STAIR.get(), new Properties())
   );
   public static final DeferredItem<Item> GLASS_BRIDGE_STAIR = ITEMS.register(
      "glass_bridge_stair", () -> new BlockItem((Block)BlockInit.GLASS_BRIDGE_STAIR.get(), new Properties())
   );
}
