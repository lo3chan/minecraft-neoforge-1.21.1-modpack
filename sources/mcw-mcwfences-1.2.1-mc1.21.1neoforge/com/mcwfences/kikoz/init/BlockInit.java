package com.mcwfences.kikoz.init;

import com.mcwfences.kikoz.objects.DoubleGate;
import com.mcwfences.kikoz.objects.FenceHitbox;
import com.mcwfences.kikoz.objects.MetalFence;
import com.mcwfences.kikoz.objects.MetalFenceMiddle;
import com.mcwfences.kikoz.objects.StoneWiredFence;
import com.mcwfences.kikoz.objects.WiredFence;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwfences");
   public static final DeferredBlock<Block> OAK_PICKET_FENCE = BLOCKS.register(
      "oak_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_PICKET_FENCE = BLOCKS.register(
      "spruce_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_PICKET_FENCE = BLOCKS.register(
      "birch_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_PICKET_FENCE = BLOCKS.register(
      "jungle_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_PICKET_FENCE = BLOCKS.register(
      "acacia_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_PICKET_FENCE = BLOCKS.register(
      "dark_oak_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_PICKET_FENCE = BLOCKS.register(
      "crimson_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_PICKET_FENCE = BLOCKS.register(
      "warped_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_PICKET_FENCE = BLOCKS.register(
      "mangrove_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_STOCKADE_FENCE = BLOCKS.register(
      "oak_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_STOCKADE_FENCE = BLOCKS.register(
      "spruce_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_STOCKADE_FENCE = BLOCKS.register(
      "birch_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_STOCKADE_FENCE = BLOCKS.register(
      "jungle_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_STOCKADE_FENCE = BLOCKS.register(
      "acacia_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_STOCKADE_FENCE = BLOCKS.register(
      "dark_oak_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_STOCKADE_FENCE = BLOCKS.register(
      "crimson_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_STOCKADE_FENCE = BLOCKS.register(
      "warped_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_STOCKADE_FENCE = BLOCKS.register(
      "mangrove_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_HORSE_FENCE = BLOCKS.register(
      "oak_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_HORSE_FENCE = BLOCKS.register(
      "spruce_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_HORSE_FENCE = BLOCKS.register(
      "birch_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_HORSE_FENCE = BLOCKS.register(
      "jungle_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_HORSE_FENCE = BLOCKS.register(
      "acacia_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_HORSE_FENCE = BLOCKS.register(
      "dark_oak_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_HORSE_FENCE = BLOCKS.register(
      "crimson_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_HORSE_FENCE = BLOCKS.register(
      "warped_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_HORSE_FENCE = BLOCKS.register(
      "mangrove_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_WIRED_FENCE = BLOCKS.register(
      "oak_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_WIRED_FENCE = BLOCKS.register(
      "spruce_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_WIRED_FENCE = BLOCKS.register(
      "birch_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_WIRED_FENCE = BLOCKS.register(
      "jungle_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_WIRED_FENCE = BLOCKS.register(
      "acacia_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_WIRED_FENCE = BLOCKS.register(
      "dark_oak_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_WIRED_FENCE = BLOCKS.register(
      "crimson_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_WIRED_FENCE = BLOCKS.register(
      "warped_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_WIRED_FENCE = BLOCKS.register(
      "mangrove_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_HEDGE = BLOCKS.register(
      "oak_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_LEAVES))
   );
   public static final DeferredBlock<Block> SPRUCE_HEDGE = BLOCKS.register(
      "spruce_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_LEAVES))
   );
   public static final DeferredBlock<Block> BIRCH_HEDGE = BLOCKS.register(
      "birch_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_LEAVES))
   );
   public static final DeferredBlock<Block> JUNGLE_HEDGE = BLOCKS.register(
      "jungle_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_LEAVES))
   );
   public static final DeferredBlock<Block> ACACIA_HEDGE = BLOCKS.register(
      "acacia_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_LEAVES))
   );
   public static final DeferredBlock<Block> DARK_OAK_HEDGE = BLOCKS.register(
      "dark_oak_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_LEAVES))
   );
   public static final DeferredBlock<Block> MANGROVE_HEDGE = BLOCKS.register(
      "mangrove_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_LEAVES))
   );
   public static final DeferredBlock<Block> AZALEA_HEDGE = BLOCKS.register(
      "azalea_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AZALEA_LEAVES))
   );
   public static final DeferredBlock<Block> FLOWERING_AZALEA_HEDGE = BLOCKS.register(
      "flowering_azalea_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWERING_AZALEA_LEAVES))
   );
   public static final DeferredBlock<Block> BAMBOO_FENCE = BLOCKS.register(
      "bamboo_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_HIGHLEY_GATE = BLOCKS.register(
      "oak_highley_gate", () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_HIGHLEY_GATE = BLOCKS.register(
      "spruce_highley_gate", () -> new FenceGateBlock(WoodType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_HIGHLEY_GATE = BLOCKS.register(
      "birch_highley_gate", () -> new FenceGateBlock(WoodType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_HIGHLEY_GATE = BLOCKS.register(
      "jungle_highley_gate", () -> new FenceGateBlock(WoodType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_HIGHLEY_GATE = BLOCKS.register(
      "acacia_highley_gate", () -> new FenceGateBlock(WoodType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_HIGHLEY_GATE = BLOCKS.register(
      "dark_oak_highley_gate", () -> new FenceGateBlock(WoodType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_HIGHLEY_GATE = BLOCKS.register(
      "crimson_highley_gate", () -> new FenceGateBlock(WoodType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_HIGHLEY_GATE = BLOCKS.register(
      "warped_highley_gate", () -> new FenceGateBlock(WoodType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_HIGHLEY_GATE = BLOCKS.register(
      "mangrove_highley_gate", () -> new FenceGateBlock(WoodType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> OAK_PYRAMID_GATE = BLOCKS.register(
      "oak_pyramid_gate", () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> SPRUCE_PYRAMID_GATE = BLOCKS.register(
      "spruce_pyramid_gate", () -> new FenceGateBlock(WoodType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS))
   );
   public static final DeferredBlock<Block> BIRCH_PYRAMID_GATE = BLOCKS.register(
      "birch_pyramid_gate", () -> new FenceGateBlock(WoodType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS))
   );
   public static final DeferredBlock<Block> JUNGLE_PYRAMID_GATE = BLOCKS.register(
      "jungle_pyramid_gate", () -> new FenceGateBlock(WoodType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS))
   );
   public static final DeferredBlock<Block> ACACIA_PYRAMID_GATE = BLOCKS.register(
      "acacia_pyramid_gate", () -> new FenceGateBlock(WoodType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS))
   );
   public static final DeferredBlock<Block> DARK_OAK_PYRAMID_GATE = BLOCKS.register(
      "dark_oak_pyramid_gate", () -> new FenceGateBlock(WoodType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS))
   );
   public static final DeferredBlock<Block> CRIMSON_PYRAMID_GATE = BLOCKS.register(
      "crimson_pyramid_gate", () -> new FenceGateBlock(WoodType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS))
   );
   public static final DeferredBlock<Block> WARPED_PYRAMID_GATE = BLOCKS.register(
      "warped_pyramid_gate", () -> new FenceGateBlock(WoodType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_PLANKS))
   );
   public static final DeferredBlock<Block> MANGROVE_PYRAMID_GATE = BLOCKS.register(
      "mangrove_pyramid_gate", () -> new FenceGateBlock(WoodType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS))
   );
   public static final DeferredBlock<Block> MODERN_STONE_BRICK_WALL = BLOCKS.register(
      "modern_stone_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> MODERN_ANDESITE_WALL = BLOCKS.register(
      "modern_andesite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> MODERN_DIORITE_WALL = BLOCKS.register(
      "modern_diorite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> MODERN_GRANITE_WALL = BLOCKS.register(
      "modern_granite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> MODERN_SANDSTONE_WALL = BLOCKS.register(
      "modern_sandstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> MODERN_RED_SANDSTONE_WALL = BLOCKS.register(
      "modern_red_sandstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> MODERN_BLACKSTONE_WALL = BLOCKS.register(
      "modern_blackstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> MODERN_NETHER_BRICK_WALL = BLOCKS.register(
      "modern_nether_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> MODERN_END_BRICK_WALL = BLOCKS.register(
      "modern_end_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> MODERN_DEEPSLATE_WALL = BLOCKS.register(
      "modern_deepslate_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> MODERN_DEEPSLATE_BRICK_WALL = BLOCKS.register(
      "modern_deepslate_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> MODERN_QUARTZ_WALL = BLOCKS.register(
      "modern_quartz_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK))
   );
   public static final DeferredBlock<Block> MODERN_MUD_BRICK_WALL = BLOCKS.register(
      "modern_mud_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> MODERN_PRISMARINE_WALL = BLOCKS.register(
      "modern_prismarine_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE))
   );
   public static final DeferredBlock<Block> RAILING_STONE_BRICK_WALL = BLOCKS.register(
      "railing_stone_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> RAILING_ANDESITE_WALL = BLOCKS.register(
      "railing_andesite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> RAILING_DIORITE_WALL = BLOCKS.register(
      "railing_diorite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> RAILING_GRANITE_WALL = BLOCKS.register(
      "railing_granite_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> RAILING_SANDSTONE_WALL = BLOCKS.register(
      "railing_sandstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> RAILING_RED_SANDSTONE_WALL = BLOCKS.register(
      "railing_red_sandstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> RAILING_BLACKSTONE_WALL = BLOCKS.register(
      "railing_blackstone_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> RAILING_NETHER_BRICK_WALL = BLOCKS.register(
      "railing_nether_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> RAILING_END_BRICK_WALL = BLOCKS.register(
      "railing_end_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> RAILING_DEEPSLATE_WALL = BLOCKS.register(
      "railing_deepslate_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> RAILING_DEEPSLATE_BRICK_WALL = BLOCKS.register(
      "railing_deepslate_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS))
   );
   public static final DeferredBlock<Block> RAILING_QUARTZ_WALL = BLOCKS.register(
      "railing_quartz_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK))
   );
   public static final DeferredBlock<Block> RAILING_MUD_BRICK_WALL = BLOCKS.register(
      "railing_mud_brick_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> RAILING_PRISMARINE_WALL = BLOCKS.register(
      "railing_prismarine_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE))
   );
   public static final DeferredBlock<Block> STONE_BRICK_RAILING_GATE = BLOCKS.register(
      "stone_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> ANDESITE_RAILING_GATE = BLOCKS.register(
      "andesite_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> DIORITE_RAILING_GATE = BLOCKS.register(
      "diorite_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> GRANITE_RAILING_GATE = BLOCKS.register(
      "granite_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> SANDSTONE_RAILING_GATE = BLOCKS.register(
      "sandstone_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_RAILING_GATE = BLOCKS.register(
      "red_sandstone_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> BLACKSTONE_RAILING_GATE = BLOCKS.register(
      "blackstone_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> BLACKSTONE_BRICK_RAILING_GATE = BLOCKS.register(
      "blackstone_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> NETHER_BRICK_RAILING_GATE = BLOCKS.register(
      "nether_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> END_BRICK_RAILING_GATE = BLOCKS.register(
      "end_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> DEEPSLATE_RAILING_GATE = BLOCKS.register(
      "deepslate_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_RAILING_GATE = BLOCKS.register(
      "deepslate_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> QUARTZ_RAILING_GATE = BLOCKS.register(
      "quartz_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> MUD_BRICK_RAILING_GATE = BLOCKS.register(
      "mud_brick_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> PRISMARINE_RAILING_GATE = BLOCKS.register(
      "prismarine_railing_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> CHERRY_PICKET_FENCE = BLOCKS.register(
      "cherry_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_STOCKADE_FENCE = BLOCKS.register(
      "cherry_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_HORSE_FENCE = BLOCKS.register(
      "cherry_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_WIRED_FENCE = BLOCKS.register(
      "cherry_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_HEDGE = BLOCKS.register(
      "cherry_hedge", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_LEAVES))
   );
   public static final DeferredBlock<Block> CHERRY_HIGHLEY_GATE = BLOCKS.register(
      "cherry_highley_gate", () -> new FenceGateBlock(WoodType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> CHERRY_PYRAMID_GATE = BLOCKS.register(
      "cherry_pyramid_gate", () -> new FenceGateBlock(WoodType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_PICKET_FENCE = BLOCKS.register(
      "bamboo_picket_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_STOCKADE_FENCE = BLOCKS.register(
      "bamboo_stockade_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_HORSE_FENCE = BLOCKS.register(
      "bamboo_horse_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_WIRED_FENCE = BLOCKS.register(
      "bamboo_wired_fence", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_HIGHLEY_GATE = BLOCKS.register(
      "bamboo_highley_gate", () -> new FenceGateBlock(WoodType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> BAMBOO_PYRAMID_GATE = BLOCKS.register(
      "bamboo_pyramid_gate", () -> new FenceGateBlock(WoodType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS))
   );
   public static final DeferredBlock<Block> STONE_PILLAR_WALL = BLOCKS.register(
      "stone_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> ANDESITE_PILLAR_WALL = BLOCKS.register(
      "andesite_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> DIORITE_PILLAR_WALL = BLOCKS.register(
      "diorite_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> GRANITE_PILLAR_WALL = BLOCKS.register(
      "granite_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> SANDSTONE_PILLAR_WALL = BLOCKS.register(
      "sandstone_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_PILLAR_WALL = BLOCKS.register(
      "red_sandstone_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BLACKSTONE_PILLAR_WALL = BLOCKS.register(
      "blackstone_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> NETHER_BRICK_PILLAR_WALL = BLOCKS.register(
      "nether_brick_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> END_BRICK_PILLAR_WALL = BLOCKS.register(
      "end_brick_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_PILLAR_WALL = BLOCKS.register(
      "deepslate_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_PILLAR_WALL = BLOCKS.register(
      "deepslate_brick_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> QUARTZ_PILLAR_WALL = BLOCKS.register(
      "quartz_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK))
   );
   public static final DeferredBlock<Block> MUD_BRICK_PILLAR_WALL = BLOCKS.register(
      "mud_brick_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> PRISMARINE_PILLAR_WALL = BLOCKS.register(
      "prismarine_pillar_wall", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE))
   );
   public static final DeferredBlock<Block> STONE_GRASS_TOPPED_WALL = BLOCKS.register(
      "stone_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_BRICKS))
   );
   public static final DeferredBlock<Block> ANDESITE_GRASS_TOPPED_WALL = BLOCKS.register(
      "andesite_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ANDESITE))
   );
   public static final DeferredBlock<Block> DIORITE_GRASS_TOPPED_WALL = BLOCKS.register(
      "diorite_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DIORITE))
   );
   public static final DeferredBlock<Block> GRANITE_GRASS_TOPPED_WALL = BLOCKS.register(
      "granite_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GRANITE))
   );
   public static final DeferredBlock<Block> SANDSTONE_GRASS_TOPPED_WALL = BLOCKS.register(
      "sandstone_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_GRASS_TOPPED_WALL = BLOCKS.register(
      "red_sandstone_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RED_SANDSTONE))
   );
   public static final DeferredBlock<Block> BLACKSTONE_GRASS_TOPPED_WALL = BLOCKS.register(
      "blackstone_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BLACKSTONE))
   );
   public static final DeferredBlock<Block> NETHER_BRICK_GRASS_TOPPED_WALL = BLOCKS.register(
      "nether_brick_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.NETHER_BRICKS))
   );
   public static final DeferredBlock<Block> END_BRICK_GRASS_TOPPED_WALL = BLOCKS.register(
      "end_brick_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS))
   );
   public static final DeferredBlock<Block> DEEPSLATE_GRASS_TOPPED_WALL = BLOCKS.register(
      "deepslate_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> DEEPSLATE_BRICK_GRASS_TOPPED_WALL = BLOCKS.register(
      "deepslate_brick_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DEEPSLATE))
   );
   public static final DeferredBlock<Block> QUARTZ_GRASS_TOPPED_WALL = BLOCKS.register(
      "quartz_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK))
   );
   public static final DeferredBlock<Block> MUD_BRICK_GRASS_TOPPED_WALL = BLOCKS.register(
      "mud_brick_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD_BRICKS))
   );
   public static final DeferredBlock<Block> PRISMARINE_GRASS_TOPPED_WALL = BLOCKS.register(
      "prismarine_grass_topped_wall", () -> new FenceHitbox(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.PRISMARINE))
   );
   public static final DeferredBlock<Block> WOODEN_CHEVAL_DE_FRISE = BLOCKS.register(
      "wooden_cheval_de_frise", () -> new WiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS))
   );
   public static final DeferredBlock<Block> IRON_CHEVAL_DE_FRISE = BLOCKS.register(
      "iron_cheval_de_frise", () -> new StoneWiredFence(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BARS))
   );
   public static final DeferredBlock<Block> ACORN_METAL_FENCE = BLOCKS.register(
      "acorn_metal_fence", () -> new MetalFence(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> PANELLED_METAL_FENCE = BLOCKS.register(
      "panelled_metal_fence", () -> new MetalFence(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> MESH_METAL_FENCE = BLOCKS.register(
      "mesh_metal_fence", () -> new MetalFence(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> ORNATE_METAL_FENCE = BLOCKS.register(
      "ornate_metal_fence", () -> new MetalFence(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> GUARDIAN_METAL_FENCE = BLOCKS.register(
      "guardian_metal_fence", () -> new MetalFence(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> GOTHIC_METAL_FENCE = BLOCKS.register(
      "gothic_metal_fence", () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> VINTAGE_METAL_FENCE = BLOCKS.register(
      "vintage_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> CURVED_METAL_FENCE = BLOCKS.register(
      "curved_metal_fence", () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> TWILIGHT_METAL_FENCE = BLOCKS.register(
      "twilight_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> EXPANDED_MESH_METAL_FENCE = BLOCKS.register(
      "expanded_mesh_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> FORTRESS_METAL_FENCE = BLOCKS.register(
      "fortress_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> BASTION_METAL_FENCE = BLOCKS.register(
      "bastion_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> DOUBLE_CURVED_METAL_FENCE = BLOCKS.register(
      "double_curved_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> CATHEDRAL_METAL_FENCE = BLOCKS.register(
      "cathedral_metal_fence",
      () -> new MetalFenceMiddle(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion())
   );
   public static final DeferredBlock<Block> MAJESTIC_METAL_FENCE_GATE = BLOCKS.register(
      "majestic_metal_fence_gate",
      () -> new DoubleGate(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> CURVED_METAL_FENCE_GATE = BLOCKS.register(
      "curved_metal_fence_gate",
      () -> new DoubleGate(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> MESH_METAL_FENCE_GATE = BLOCKS.register(
      "mesh_metal_fence_gate",
      () -> new DoubleGate(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> PANELLED_METAL_FENCE_GATE = BLOCKS.register(
      "panelled_metal_fence_gate",
      () -> new DoubleGate(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> BASTION_METAL_FENCE_GATE = BLOCKS.register(
      "bastion_metal_fence_gate",
      () -> new DoubleGate(Properties.of().requiresCorrectToolForDrops().strength(2.0F, 3.0F).sound(SoundType.METAL).noOcclusion().forceSolidOn())
   );
   public static final DeferredBlock<Block> OAK_CURVED_GATE = BLOCKS.register(
      "oak_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> SPRUCE_CURVED_GATE = BLOCKS.register(
      "spruce_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BIRCH_CURVED_GATE = BLOCKS.register(
      "birch_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> JUNGLE_CURVED_GATE = BLOCKS.register(
      "jungle_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> ACACIA_CURVED_GATE = BLOCKS.register(
      "acacia_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> DARK_OAK_CURVED_GATE = BLOCKS.register(
      "dark_oak_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> CRIMSON_CURVED_GATE = BLOCKS.register(
      "crimson_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> WARPED_CURVED_GATE = BLOCKS.register(
      "warped_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> MANGROVE_CURVED_GATE = BLOCKS.register(
      "mangrove_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> CHERRY_CURVED_GATE = BLOCKS.register(
      "cherry_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
   public static final DeferredBlock<Block> BAMBOO_CURVED_GATE = BLOCKS.register(
      "bamboo_curved_gate", () -> new DoubleGate(Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
   );
}
