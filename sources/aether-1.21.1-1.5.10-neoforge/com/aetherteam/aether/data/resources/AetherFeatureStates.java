package com.aetherteam.aether.data.resources;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AetherFeatureStates {
   public static final BlockState COLD_AERCLOUD = (BlockState)((Block)AetherBlocks.COLD_AERCLOUD.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState BLUE_AERCLOUD = (BlockState)((Block)AetherBlocks.BLUE_AERCLOUD.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState GOLDEN_AERCLOUD = (BlockState)((Block)AetherBlocks.GOLDEN_AERCLOUD.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState SKYROOT_LOG = (BlockState)((RotatedPillarBlock)AetherBlocks.SKYROOT_LOG.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState SKYROOT_LEAVES = (BlockState)((Block)AetherBlocks.SKYROOT_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState GOLDEN_OAK_LOG = (BlockState)((RotatedPillarBlock)AetherBlocks.GOLDEN_OAK_LOG.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState GOLDEN_OAK_LEAVES = (BlockState)((Block)AetherBlocks.GOLDEN_OAK_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState CRYSTAL_LEAVES = (BlockState)((Block)AetherBlocks.CRYSTAL_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState CRYSTAL_FRUIT_LEAVES = (BlockState)((Block)AetherBlocks.CRYSTAL_FRUIT_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState HOLIDAY_LEAVES = (BlockState)((Block)AetherBlocks.HOLIDAY_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState DECORATED_HOLIDAY_LEAVES = (BlockState)((Block)AetherBlocks.DECORATED_HOLIDAY_LEAVES.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState SNOW = Blocks.SNOW.defaultBlockState();
   public static final BlockState PRESENT = ((Block)AetherBlocks.PRESENT.get()).defaultBlockState();
   public static final BlockState AIR = Blocks.AIR.defaultBlockState();
   public static final BlockState PURPLE_FLOWER = ((Block)AetherBlocks.PURPLE_FLOWER.get()).defaultBlockState();
   public static final BlockState WHITE_FLOWER = ((Block)AetherBlocks.WHITE_FLOWER.get()).defaultBlockState();
   public static final BlockState BERRY_BUSH = (BlockState)((Block)AetherBlocks.BERRY_BUSH.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState QUICKSOIL = (BlockState)((Block)AetherBlocks.QUICKSOIL.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState AETHER_GRASS_BLOCK = (BlockState)((Block)AetherBlocks.AETHER_GRASS_BLOCK.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState AETHER_DIRT = (BlockState)((Block)AetherBlocks.AETHER_DIRT.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState HOLYSTONE = (BlockState)((Block)AetherBlocks.HOLYSTONE.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState ICESTONE = ((Block)AetherBlocks.ICESTONE.get()).defaultBlockState();
   public static final BlockState AMBROSIUM_ORE = (BlockState)((Block)AetherBlocks.AMBROSIUM_ORE.get())
      .defaultBlockState()
      .setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   public static final BlockState ZANITE_ORE = ((Block)AetherBlocks.ZANITE_ORE.get()).defaultBlockState();
   public static final BlockState GRAVITITE_ORE = ((Block)AetherBlocks.GRAVITITE_ORE.get()).defaultBlockState();
}
