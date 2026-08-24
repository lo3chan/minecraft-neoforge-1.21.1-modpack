package dev.worldgen.lithostitched.api.worldgen.poolelement;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingElementBuilderImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public interface DelegatingElementBuilder {
   static DelegatingElementBuilder create(StructurePoolElement delegate) {
      return new DelegatingElementBuilderImpl(delegate);
   }

   DelegatingElementBuilder forcedCount(int var1);

   DelegatingElementBuilder limitedCount(int var1);

   DelegatingElementBuilder allowedDepth(InclusiveRange<Integer> var1);

   DelegatingElementBuilder terrainAdaptation(TerrainAdjustment var1);

   DelegatingElementBuilder condition(PlacementCondition var1);

   DelegatingElementBuilder named(ResourceLocation var1);

   DelegatingElementBuilder terrainMatchingHeightmap(Types var1);

   DelegatingElementBuilder allowBoundingBoxCollisions();

   DelegatingElementBuilder otherPiecesCanIntersect();

   DelegatingConfig build();
}
