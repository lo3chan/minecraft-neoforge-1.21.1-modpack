package net.joefoxe.hexerei.world.processor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@MethodsReturnNonnullByDefault
public class DarkCovenLegProcessor extends StructureProcessor {
   public static final MapCodec<DarkCovenLegProcessor> CODEC = MapCodec.unit(DarkCovenLegProcessor::new);

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
      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)ModStructureProcessors.DARK_COVEN_LEG_PROCESSOR.get();
   }

   private static StructureBlockInfo getReturnBlock(BlockPos worldPos, BlockState originalReplacementState) {
      return originalReplacementState != null && !originalReplacementState.is(Blocks.STRUCTURE_VOID)
         ? new StructureBlockInfo(worldPos, originalReplacementState, null)
         : null;
   }
}
