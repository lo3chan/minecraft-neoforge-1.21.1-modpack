package com.yungnickyoung.minecraft.betterdungeons.world.processor.zombie_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import com.yungnickyoung.minecraft.yungsapi.world.structure.processor.ISafeWorldModifier;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ZombieDungeonLegProcessor extends StructureProcessor implements ISafeWorldModifier {
   public static final ZombieDungeonLegProcessor INSTANCE = new ZombieDungeonLegProcessor();
   public static final MapCodec<ZombieDungeonLegProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
   private static final BlockStateRandomizer LEG_SELECTOR = new BlockStateRandomizer(Blocks.COBBLESTONE.defaultBlockState())
      .addBlock(Blocks.POLISHED_ANDESITE.defaultBlockState(), 0.8F);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.MAGENTA_STAINED_GLASS) {
         if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos()))) {
            return blockInfoGlobal;
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         Optional<BlockState> blockState = this.getBlockStateSafe(levelReader, blockInfoGlobal.pos());
         if (!blockState.isEmpty() && !blockState.get().isAir() && !blockState.get().liquid()) {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), blockState.get(), blockInfoGlobal.nbt());
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SMOOTH_STONE.defaultBlockState(), null);
         }

         MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);

         for (BlockState currBlockState = levelReader.getBlockState(mutable);
            mutable.getY() > levelReader.getMinBuildHeight()
               && mutable.getY() < levelReader.getMaxBuildHeight()
               && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty());
            currBlockState = levelReader.getBlockState(mutable)
         ) {
            levelReader.getChunk(mutable).setBlockState(mutable, LEG_SELECTOR.get(random), false);
            mutable.move(Direction.DOWN);
         }
      } else if (blockInfoGlobal.state().getBlock() == Blocks.PURPUR_SLAB) {
         Optional<BlockState> blockState = this.getBlockStateSafe(levelReader, blockInfoGlobal.pos());
         if (!blockState.isEmpty() && !blockState.get().isAir() && !blockState.get().liquid()) {
            blockInfoGlobal = null;
         } else {
            blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), null);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.ZOMBIE_DUNGEON_LEG_PROCESSOR;
   }
}
