package com.yungnickyoung.minecraft.betterwitchhuts.mixin;

import com.yungnickyoung.minecraft.betterwitchhuts.BetterWitchHutsCommon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkGenerator.class})
public class DisableVanillaWitchHutsMixin {
   @Inject(
      method = {"tryGenerateStructure"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void betterwitchhuts_disableVanillaWitchHuts(
      StructureSelectionEntry structureSetEntry,
      StructureManager structureManager,
      RegistryAccess registryAccess,
      RandomState randomState,
      StructureTemplateManager structureTemplateManager,
      long seed,
      ChunkAccess chunkAccess,
      ChunkPos chunkPos,
      SectionPos sectionPos,
      CallbackInfoReturnable<Boolean> cir
   ) {
      if (BetterWitchHutsCommon.CONFIG.general.disableVanillaWitchHuts && ((Structure)structureSetEntry.structure().value()).type() == StructureType.SWAMP_HUT) {
         cir.setReturnValue(false);
      }
   }
}
