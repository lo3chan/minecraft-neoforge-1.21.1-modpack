package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.block.AetherBlocks;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class HolystoneReplaceProcessor extends StructureProcessor {
   public static final MapCodec<HolystoneReplaceProcessor> CODEC = MapCodec.unit(() -> HolystoneReplaceProcessor.INSTANCE);
   public static final HolystoneReplaceProcessor INSTANCE = new HolystoneReplaceProcessor();
   private final Map<Block, Block> replacements = (Map<Block, Block>)Util.make(Maps.newHashMap(), map -> {
      map.put(Blocks.COBBLESTONE, (Block)AetherBlocks.HOLYSTONE_BRICKS.get());
      map.put(Blocks.MOSSY_COBBLESTONE, (Block)AetherBlocks.HOLYSTONE_BRICKS.get());
      map.put(Blocks.COBBLESTONE_STAIRS, (Block)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get());
      map.put(Blocks.MOSSY_COBBLESTONE_STAIRS, (Block)AetherBlocks.HOLYSTONE_BRICK_STAIRS.get());
      map.put(Blocks.COBBLESTONE_SLAB, (Block)AetherBlocks.HOLYSTONE_BRICK_SLAB.get());
      map.put(Blocks.MOSSY_COBBLESTONE_SLAB, (Block)AetherBlocks.HOLYSTONE_BRICK_SLAB.get());
      map.put(Blocks.COBBLESTONE_WALL, (Block)AetherBlocks.HOLYSTONE_BRICK_WALL.get());
      map.put(Blocks.MOSSY_COBBLESTONE_WALL, (Block)AetherBlocks.HOLYSTONE_BRICK_WALL.get());
   });

   private HolystoneReplaceProcessor() {
   }

   @Nullable
   public StructureBlockInfo process(
      LevelReader level,
      BlockPos otherPos,
      BlockPos pos,
      StructureBlockInfo blockInfo,
      StructureBlockInfo relativeBlockInfo,
      StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      Block block = this.replacements.get(relativeBlockInfo.state().getBlock());
      if (block == null) {
         return relativeBlockInfo;
      } else {
         BlockState originalState = relativeBlockInfo.state();
         BlockState newState = block.defaultBlockState();
         if (originalState.hasProperty(StairBlock.FACING)) {
            newState = (BlockState)newState.setValue(StairBlock.FACING, (Direction)originalState.getValue(StairBlock.FACING));
         }

         if (originalState.hasProperty(StairBlock.HALF)) {
            newState = (BlockState)newState.setValue(StairBlock.HALF, (Half)originalState.getValue(StairBlock.HALF));
         }

         if (originalState.hasProperty(SlabBlock.TYPE)) {
            newState = (BlockState)newState.setValue(SlabBlock.TYPE, (SlabType)originalState.getValue(SlabBlock.TYPE));
         }

         return new StructureBlockInfo(relativeBlockInfo.pos(), newState, relativeBlockInfo.nbt());
      }
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)AetherStructureProcessors.HOLYSTONE_REPLACE.get();
   }
}
