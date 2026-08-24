package com.yungnickyoung.minecraft.yungsapi.api;

import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.JigsawManager;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class YungJigsawManager {
   public static Optional<GenerationStub> assembleJigsawStructure(
      GenerationContext generationContext,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawNameOptional,
      int maxDepth,
      BlockPos startPos,
      boolean useExpansionHack,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter,
      Optional<Integer> maxY,
      Optional<Integer> minY,
      DimensionPadding dimensionPadding,
      LiquidSettings liquidSettings
   ) {
      return JigsawManager.assembleJigsawStructure(
         generationContext,
         startPool,
         startJigsawNameOptional,
         maxDepth,
         startPos,
         useExpansionHack,
         projectStartToHeightmap,
         maxDistanceFromCenter,
         maxY,
         minY,
         dimensionPadding,
         liquidSettings
      );
   }
}
