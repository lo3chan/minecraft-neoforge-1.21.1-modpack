package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class OrangeStainedGlassProcessor extends StructureProcessor {
   public static final OrangeStainedGlassProcessor INSTANCE = new OrangeStainedGlassProcessor();
   public static final MapCodec<OrangeStainedGlassProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.ORANGE_STAINED_GLASS) {
         RandomSource randomSource = structurePlacementData.getRandom(blockInfoGlobal.pos());
         float f = randomSource.nextFloat();
         if (f < 0.01F) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("LootTable", "minecraft:archaeology/desert_pyramid");
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SUSPICIOUS_SAND.defaultBlockState(), nbt);
         } else if (f < 0.1F) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SAND.defaultBlockState(), blockInfoGlobal.nbt());
         } else if (f < 0.2F) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SANDSTONE.defaultBlockState(), blockInfoGlobal.nbt());
         } else if (f < 0.45F) {
            BlockState state = (BlockState)((BlockState)((BlockState)Blocks.SANDSTONE_STAIRS
                     .defaultBlockState()
                     .setValue(StairBlock.FACING, Plane.HORIZONTAL.getRandomDirection(randomSource)))
                  .setValue(StairBlock.HALF, Half.TOP))
               .setValue(StairBlock.SHAPE, StairsShape.STRAIGHT);
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), state, blockInfoGlobal.nbt());
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.CUT_SANDSTONE.defaultBlockState(), blockInfoGlobal.nbt());
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorModule.ORANGE_STAINED_GLASS_PROCESSOR;
   }
}
