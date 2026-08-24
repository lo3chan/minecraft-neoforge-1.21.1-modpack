package com.yungnickyoung.minecraft.betterendisland.world.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterendisland.module.StructureProcessorTypeModule;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ObsidianProcessor extends StructureProcessor {
   public static final MapCodec<ObsidianProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("number_times_dragon_killed").forGetter(config -> config.numberTimesDragonKilled))
         .apply(instance, instance.stable(ObsidianProcessor::new))
   );
   private final int numberTimesDragonKilled;

   public ObsidianProcessor(int numberTimesDragonKilled) {
      this.numberTimesDragonKilled = numberTimesDragonKilled;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().is(Blocks.OBSIDIAN)) {
         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         BlockState outputState = Blocks.OBSIDIAN.defaultBlockState();
         int dragonKills = Mth.clamp(this.numberTimesDragonKilled, 0, 10);
         float cryingChance = Mth.lerp(dragonKills / 10.0F, 0.0F, 0.5F);
         if (random.nextFloat() < cryingChance) {
            outputState = Blocks.CRYING_OBSIDIAN.defaultBlockState();
         }

         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), outputState, blockInfoGlobal.nbt());
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.OBSIDIAN_PROCESSOR;
   }
}
