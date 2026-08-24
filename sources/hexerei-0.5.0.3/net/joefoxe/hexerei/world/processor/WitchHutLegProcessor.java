package net.joefoxe.hexerei.world.processor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.MahoganyLog;
import net.joefoxe.hexerei.block.custom.WillowLog;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@MethodsReturnNonnullByDefault
public class WitchHutLegProcessor extends StructureProcessor {
   public static final MapCodec<WitchHutLegProcessor> CODEC = MapCodec.unit(WitchHutLegProcessor::new);

   @ParametersAreNonnullByDefault
   public StructureBlockInfo process(
      LevelReader worldReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData,
      @Nullable StructureTemplate template
   ) {
      if (blockInfoGlobal.state().getBlock() == Blocks.WHITE_STAINED_GLASS_PANE) {
         BlockPos worldPos = blockInfoGlobal.pos();
         MutableBlockPos currentPos = new MutableBlockPos().set(worldPos);
         if (worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
            return getReturnBlock(worldPos, blockInfoGlobal.state());
         }

         ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
         ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         currentChunk.setBlockState(blockInfoGlobal.pos(), Blocks.SPRUCE_LOG.defaultBlockState(), false);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SPRUCE_LOG.defaultBlockState(), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();

         for (BlockState currBlock = worldReader.getBlockState(mutable);
            mutable.getY() > worldReader.getMinBuildHeight()
               && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA));
            currBlock = worldReader.getBlockState(mutable)
         ) {
            currentChunk.setBlockState(mutable, Blocks.SPRUCE_LOG.defaultBlockState(), false);
            mutable.move(Direction.DOWN);
         }
      } else if (blockInfoGlobal.state().getBlock() == Blocks.RED_STAINED_GLASS_PANE) {
         BlockPos worldPos = blockInfoGlobal.pos();
         MutableBlockPos currentPos = new MutableBlockPos().set(worldPos);
         if (worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
            return getReturnBlock(worldPos, blockInfoGlobal.state());
         }

         ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
         ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         currentChunk.setBlockState(blockInfoGlobal.pos(), ((MahoganyLog)ModBlocks.MAHOGANY_LOG.get()).defaultBlockState(), false);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), ((MahoganyLog)ModBlocks.MAHOGANY_LOG.get()).defaultBlockState(), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();

         for (BlockState currBlock = worldReader.getBlockState(mutable);
            mutable.getY() > 0
               && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA));
            currBlock = worldReader.getBlockState(mutable)
         ) {
            currentChunk.setBlockState(mutable, ((MahoganyLog)ModBlocks.MAHOGANY_LOG.get()).defaultBlockState(), false);
            mutable.move(Direction.DOWN);
         }
      } else if (blockInfoGlobal.state().getBlock() == Blocks.YELLOW_STAINED_GLASS_PANE) {
         BlockPos worldPos = blockInfoGlobal.pos();
         MutableBlockPos currentPos = new MutableBlockPos().set(worldPos);
         if (worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
            return getReturnBlock(worldPos, blockInfoGlobal.state());
         }

         ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
         ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         currentChunk.setBlockState(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), false);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();

         for (BlockState currBlock = worldReader.getBlockState(mutable);
            mutable.getY() > 0
               && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA));
            currBlock = worldReader.getBlockState(mutable)
         ) {
            currentChunk.setBlockState(mutable, Blocks.OAK_LOG.defaultBlockState(), false);
            mutable.move(Direction.DOWN);
         }
      } else if (blockInfoGlobal.state().getBlock() == Blocks.PURPLE_STAINED_GLASS_PANE) {
         BlockPos worldPos = blockInfoGlobal.pos();
         MutableBlockPos currentPos = new MutableBlockPos().set(worldPos);
         if (worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))) {
            return getReturnBlock(worldPos, blockInfoGlobal.state());
         }

         ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
         ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         currentChunk.setBlockState(blockInfoGlobal.pos(), ((WillowLog)ModBlocks.WILLOW_LOG.get()).defaultBlockState(), false);
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), ((WillowLog)ModBlocks.WILLOW_LOG.get()).defaultBlockState(), blockInfoGlobal.nbt());
         MutableBlockPos mutable = blockInfoGlobal.pos().below().mutable();

         for (BlockState currBlock = worldReader.getBlockState(mutable);
            mutable.getY() > 0
               && (currBlock.canBeReplaced() || currBlock.isAir() || currBlock.is(BlockTags.LEAVES) || currBlock.is(Blocks.WATER) || currBlock.is(Blocks.LAVA));
            currBlock = worldReader.getBlockState(mutable)
         ) {
            currentChunk.setBlockState(mutable, ((WillowLog)ModBlocks.WILLOW_LOG.get()).defaultBlockState(), false);
            mutable.move(Direction.DOWN);
         }
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)ModStructureProcessors.WITCH_HUT_LEG_PROCESSOR.get();
   }

   private static StructureBlockInfo getReturnBlock(BlockPos worldPos, BlockState originalReplacementState) {
      return originalReplacementState != null && !originalReplacementState.is(Blocks.STRUCTURE_VOID)
         ? new StructureBlockInfo(worldPos, originalReplacementState, null)
         : null;
   }
}
