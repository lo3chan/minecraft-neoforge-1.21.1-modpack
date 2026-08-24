package com.mcwbridges.kikoz.init;

import com.mcwbridges.kikoz.objects.Bamboo_Bridge;
import com.mcwbridges.kikoz.objects.Bridge_Block;
import com.mcwbridges.kikoz.objects.Bridge_Block_Rope;
import com.mcwbridges.kikoz.objects.Bridge_Stairs;
import com.mcwbridges.kikoz.objects.Bridge_Support;
import com.mcwbridges.kikoz.objects.Iron_Bridge;
import com.mcwbridges.kikoz.objects.Log_Bridge;
import com.mcwbridges.kikoz.objects.Rail_Bridge;
import com.mcwbridges.kikoz.objects.items.Bridge_Lantern;
import com.mcwbridges.kikoz.objects.items.Bridge_Torch;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwbridges");
   public static final DeferredBlock<Block> IRON_BRIDGE = BLOCKS.register(
      "iron_bridge", () -> new Iron_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK))
   );
   public static final DeferredBlock<Block> OAK_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "oak_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "birch_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "acacia_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "spruce_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "jungle_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "dark_oak_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "crimson_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "warped_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "mangrove_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_OAK_BRIDGE = BLOCKS.register(
      "rope_oak_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_BIRCH_BRIDGE = BLOCKS.register(
      "rope_birch_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_SPRUCE_BRIDGE = BLOCKS.register(
      "rope_spruce_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_JUNGLE_BRIDGE = BLOCKS.register(
      "rope_jungle_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_ACACIA_BRIDGE = BLOCKS.register(
      "rope_acacia_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_DARK_OAK_BRIDGE = BLOCKS.register(
      "rope_dark_oak_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_CRIMSON_BRIDGE = BLOCKS.register(
      "rope_crimson_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_WARPED_BRIDGE = BLOCKS.register(
      "rope_warped_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_MANGROVE_BRIDGE = BLOCKS.register(
      "rope_mangrove_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> BRICK_BRIDGE = BLOCKS.register(
      "brick_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BRICKS))
   );
   public static final DeferredBlock<Block> SANDSTONE_BRIDGE = BLOCKS.register(
      "sandstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> STONE_BRICK_BRIDGE = BLOCKS.register(
      "stone_brick_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> ORANGE_SANDSTONE_BRIDGE = BLOCKS.register(
      "orange_sandstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BLACKSTONE_BRIDGE = BLOCKS.register(
      "blackstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> MOSSY_STONE_BRICK_BRIDGE = BLOCKS.register(
      "mossy_stone_brick_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_BRIDGE = BLOCKS.register(
      "deepslate_brick_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_TILE_BRIDGE = BLOCKS.register(
      "deepslate_tile_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_TILES))
   );
   public static final DeferredBlock<Block> MUD_BRICK_BRIDGE = BLOCKS.register(
      "mud_brick_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> OAK_RAIL_BRIDGE = BLOCKS.register(
      "oak_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_RAIL_BRIDGE = BLOCKS.register(
      "spruce_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_RAIL_BRIDGE = BLOCKS.register(
      "birch_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_RAIL_BRIDGE = BLOCKS.register(
      "jungle_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_RAIL_BRIDGE = BLOCKS.register(
      "acacia_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_RAIL_BRIDGE = BLOCKS.register(
      "dark_oak_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_RAIL_BRIDGE = BLOCKS.register(
      "crimson_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_RAIL_BRIDGE = BLOCKS.register(
      "warped_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_RAIL_BRIDGE = BLOCKS.register(
      "mangrove_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_BRIDGE = BLOCKS.register(
      "bamboo_bridge", () -> new Bamboo_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> DRY_BAMBOO_BRIDGE = BLOCKS.register(
      "dry_bamboo_bridge", () -> new Bamboo_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> IRON_BRIDGE_PIER = BLOCKS.register(
      "iron_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK))
   );
   public static final DeferredBlock<Block> OAK_BRIDGE_PIER = BLOCKS.register(
      "oak_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_BRIDGE_PIER = BLOCKS.register(
      "spruce_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_BRIDGE_PIER = BLOCKS.register(
      "birch_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_BRIDGE_PIER = BLOCKS.register(
      "jungle_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_BRIDGE_PIER = BLOCKS.register(
      "acacia_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_BRIDGE_PIER = BLOCKS.register(
      "dark_oak_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_BRIDGE_PIER = BLOCKS.register(
      "crimson_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_BRIDGE_PIER = BLOCKS.register(
      "warped_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_BRIDGE_PIER = BLOCKS.register(
      "mangrove_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> BRICK_BRIDGE_PIER = BLOCKS.register(
      "brick_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BRICKS))
   );
   public static final DeferredBlock<Block> SANDSTONE_BRIDGE_PIER = BLOCKS.register(
      "sandstone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> STONE_BRIDGE_PIER = BLOCKS.register(
      "stone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_BRIDGE_PIER = BLOCKS.register(
      "red_sandstone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BLACKSTONE_BRIDGE_PIER = BLOCKS.register(
      "blackstone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> MOSSY_STONE_BRIDGE_PIER = BLOCKS.register(
      "mossy_stone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_BRIDGE_PIER = BLOCKS.register(
      "deepslate_brick_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_TILE_BRIDGE_PIER = BLOCKS.register(
      "deepslate_tile_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_TILES))
   );
   public static final DeferredBlock<Block> MUD_BRICK_BRIDGE_PIER = BLOCKS.register(
      "mud_brick_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> BAMBOO_BRIDGE_PIER = BLOCKS.register(
      "bamboo_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> DRY_BAMBOO_BRIDGE_PIER = BLOCKS.register(
      "dry_bamboo_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> IRON_BRIDGE_STAIR = BLOCKS.register(
      "iron_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK))
   );
   public static final DeferredBlock<Block> OAK_LOG_BRIDGE_STAIR = BLOCKS.register(
      "oak_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_LOG_BRIDGE_STAIR = BLOCKS.register(
      "spruce_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_LOG_BRIDGE_STAIR = BLOCKS.register(
      "birch_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_LOG_BRIDGE_STAIR = BLOCKS.register(
      "jungle_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_LOG_BRIDGE_STAIR = BLOCKS.register(
      "acacia_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_LOG_BRIDGE_STAIR = BLOCKS.register(
      "dark_oak_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_LOG_BRIDGE_STAIR = BLOCKS.register(
      "crimson_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_LOG_BRIDGE_STAIR = BLOCKS.register(
      "warped_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_LOG_BRIDGE_STAIR = BLOCKS.register(
      "mangrove_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "oak_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "spruce_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "birch_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "jungle_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "acacia_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "dark_oak_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "crimson_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "warped_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "mangrove_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> STONE_BRICK_BRIDGE_STAIR = BLOCKS.register(
      "stone_brick_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> SANDSTONE_BRIDGE_STAIR = BLOCKS.register(
      "sandstone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> BRICK_BRIDGE_STAIR = BLOCKS.register(
      "brick_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BRICKS))
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_BRIDGE_STAIR = BLOCKS.register(
      "red_sandstone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BLACKSTONE_BRIDGE_STAIR = BLOCKS.register(
      "blackstone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> MOSSY_STONE_BRIDGE_STAIR = BLOCKS.register(
      "mossy_stone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_BRIDGE_STAIR = BLOCKS.register(
      "deepslate_brick_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_TILE_BRIDGE_STAIR = BLOCKS.register(
      "deepslate_tile_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_TILES))
   );
   public static final DeferredBlock<Block> MUD_BRICK_BRIDGE_STAIR = BLOCKS.register(
      "mud_brick_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> BAMBOO_BRIDGE_STAIR = BLOCKS.register(
      "bamboo_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> DRY_BAMBOO_BRIDGE_STAIR = BLOCKS.register(
      "dry_bamboo_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_LOG_BRIDGE_MIDDLE = BLOCKS.register(
      "cherry_log_bridge_middle", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> ROPE_CHERRY_BRIDGE = BLOCKS.register(
      "rope_cherry_bridge", () -> new Bridge_Block_Rope(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_RAIL_BRIDGE = BLOCKS.register(
      "cherry_rail_bridge", () -> new Rail_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_BRIDGE_PIER = BLOCKS.register(
      "cherry_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_LOG_BRIDGE_STAIR = BLOCKS.register(
      "cherry_log_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_ROPE_BRIDGE_STAIR = BLOCKS.register(
      "cherry_rope_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> BRIDGE_TORCH = BLOCKS.register(
      "bridge_torch", () -> new Bridge_Torch(Properties.of().mapColor(MapColor.WOOD).strength(0.8F, 2.0F).instabreak().sound(SoundType.WOOD), 15)
   );
   public static final DeferredBlock<Block> BRIDGE_LANTERN = BLOCKS.register(
      "bridge_lantern", () -> new Bridge_Lantern(Properties.of().mapColor(MapColor.WOOD).strength(0.8F, 2.0F).instabreak().sound(SoundType.WOOD), 15)
   );
   public static final DeferredBlock<Block> ASIAN_RED_BRIDGE = BLOCKS.register(
      "asian_red_bridge", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> GLASS_BRIDGE = BLOCKS.register(
      "glass_bridge", () -> new Log_Bridge(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS))
   );
   public static final DeferredBlock<Block> COBBLESTONE_BRIDGE = BLOCKS.register(
      "cobblestone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
   );
   public static final DeferredBlock<Block> MOSSY_COBBLESTONE_BRIDGE = BLOCKS.register(
      "mossy_cobblestone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE))
   );
   public static final DeferredBlock<Block> ANDESITE_BRIDGE = BLOCKS.register(
      "andesite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> GRANITE_BRIDGE = BLOCKS.register(
      "granite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> DIORITE_BRIDGE = BLOCKS.register(
      "diorite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICKS_BRIDGE = BLOCKS.register(
      "prismarine_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS))
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_BRIDGE = BLOCKS.register(
      "nether_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> END_STONE_BRICKS_BRIDGE = BLOCKS.register(
      "end_stone_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> COBBLESTONE_BRIDGE_PIER = BLOCKS.register(
      "cobblestone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
   );
   public static final DeferredBlock<Block> MOSSY_COBBLESTONE_BRIDGE_PIER = BLOCKS.register(
      "mossy_cobblestone_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE))
   );
   public static final DeferredBlock<Block> ANDESITE_BRIDGE_PIER = BLOCKS.register(
      "andesite_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> GRANITE_BRIDGE_PIER = BLOCKS.register(
      "granite_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> DIORITE_BRIDGE_PIER = BLOCKS.register(
      "diorite_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICKS_BRIDGE_PIER = BLOCKS.register(
      "prismarine_bricks_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS))
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_BRIDGE_PIER = BLOCKS.register(
      "nether_bricks_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> END_STONE_BRICKS_BRIDGE_PIER = BLOCKS.register(
      "end_stone_bricks_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> COBBLESTONE_BRIDGE_STAIR = BLOCKS.register(
      "cobblestone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
   );
   public static final DeferredBlock<Block> MOSSY_COBBLESTONE_BRIDGE_STAIR = BLOCKS.register(
      "mossy_cobblestone_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE))
   );
   public static final DeferredBlock<Block> ANDESITE_BRIDGE_STAIR = BLOCKS.register(
      "andesite_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> GRANITE_BRIDGE_STAIR = BLOCKS.register(
      "granite_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> DIORITE_BRIDGE_STAIR = BLOCKS.register(
      "diorite_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICKS_BRIDGE_STAIR = BLOCKS.register(
      "prismarine_bricks_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS))
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_BRIDGE_STAIR = BLOCKS.register(
      "nether_bricks_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> END_STONE_BRICKS_BRIDGE_STAIR = BLOCKS.register(
      "end_stone_bricks_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_STONE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_stone_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_SANDSTONE_BRIDGE = BLOCKS.register(
      "balustrade_sandstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_ORANGE_SANDSTONE_BRIDGE = BLOCKS.register(
      "balustrade_orange_sandstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_BLACKSTONE_BRIDGE = BLOCKS.register(
      "balustrade_blackstone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_MOSSY_STONE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_mossy_stone_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_DEEPSLATE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_deepslate_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_DEEPSLATE_TILES_BRIDGE = BLOCKS.register(
      "balustrade_deepslate_tiles_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_TILES))
   );
   public static final DeferredBlock<Block> BALUSTRADE_MUD_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_mud_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_COBBLESTONE_BRIDGE = BLOCKS.register(
      "balustrade_cobblestone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_MOSSY_COBBLESTONE_BRIDGE = BLOCKS.register(
      "balustrade_mossy_cobblestone_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_ANDESITE_BRIDGE = BLOCKS.register(
      "balustrade_andesite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_GRANITE_BRIDGE = BLOCKS.register(
      "balustrade_granite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_DIORITE_BRIDGE = BLOCKS.register(
      "balustrade_diorite_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> BALUSTRADE_PRISMARINE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_prismarine_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_NETHER_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_nether_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> BALUSTRADE_END_STONE_BRICKS_BRIDGE = BLOCKS.register(
      "balustrade_end_stone_bricks_bridge", () -> new Bridge_Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> ASIAN_RED_BRIDGE_PIER = BLOCKS.register(
      "asian_red_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> GLASS_BRIDGE_PIER = BLOCKS.register(
      "glass_bridge_pier", () -> new Bridge_Support(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS))
   );
   public static final DeferredBlock<Block> ASIAN_RED_BRIDGE_STAIR = BLOCKS.register(
      "asian_red_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> GLASS_BRIDGE_STAIR = BLOCKS.register(
      "glass_bridge_stair", () -> new Bridge_Stairs(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLASS))
   );
}
