package net.joefoxe.hexerei.world.processor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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
public class MangroveTreeLegProcessor extends StructureProcessor {
   public static final MapCodec<MangroveTreeLegProcessor> CODEC = MapCodec.unit(MangroveTreeLegProcessor::new);

   public static boolean isAirOrLeavesOrLogsAt(ChunkAccess currentChunk, BlockPos pos) {
      BlockState state = currentChunk.getBlockState(pos);
      return state.canBeReplaced() || state.isAir() || state.is(Blocks.BAMBOO) || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS);
   }

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
      ChunkPos currentChunkPos = new ChunkPos(blockInfoGlobal.pos());
      ChunkAccess currentChunk = worldReader.getChunk(currentChunkPos.x, currentChunkPos.z);
      RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
      MutableBlockPos currentPos = new MutableBlockPos().set(blockInfoGlobal.pos());
      return worldReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(currentPos))
         ? getReturnBlock(blockInfoGlobal.pos(), blockInfoGlobal.state())
         : blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)ModStructureProcessors.MANGROVE_TREE_LEG_PROCESSOR.get();
   }

   private static StructureBlockInfo getReturnBlock(BlockPos worldPos, BlockState originalReplacementState) {
      return originalReplacementState != null && !originalReplacementState.is(Blocks.STRUCTURE_VOID)
         ? new StructureBlockInfo(worldPos, originalReplacementState, null)
         : null;
   }
}
