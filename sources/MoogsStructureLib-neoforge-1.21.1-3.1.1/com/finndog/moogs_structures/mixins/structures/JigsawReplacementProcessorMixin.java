package com.finndog.moogs_structures.mixins.structures;

import com.finndog.moogs_structures.utils.DebugFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({JigsawReplacementProcessor.class})
public class JigsawReplacementProcessorMixin {
   @Inject(
      method = {"processBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void moogs_structures_keepJigsawBlocks(
      LevelReader level,
      BlockPos offset,
      BlockPos pos,
      StructureBlockInfo blockInfo,
      StructureBlockInfo relativeBlockInfo,
      StructurePlaceSettings settings,
      CallbackInfoReturnable<StructureBlockInfo> cir
   ) {
      if (DebugFlags.isKeepJigsawBlocks()) {
         cir.setReturnValue(relativeBlockInfo);
      }
   }
}
