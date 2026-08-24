package dev.worldgen.lithostitched.worldgen.poolelement;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.poolelement.DelegatingElementBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public class DelegatingElementBuilderImpl implements DelegatingElementBuilder {
   private final StructurePoolElement delegate;
   private Optional<ResourceLocation> name = Optional.empty();
   private Optional<PlacementCondition> placementCondition = Optional.empty();
   private Optional<InclusiveRange<Integer>> allowedDepth = Optional.empty();
   private Optional<Integer> forcedCount = Optional.empty();
   private Optional<Integer> maxCount = Optional.empty();
   private boolean allowBoundingBoxCollisions = false;
   private boolean otherPiecesCanIntersect = false;
   private Types terrainMatchingHeightmap = Types.WORLD_SURFACE_WG;
   private Optional<TerrainAdjustment> overrideTerrainAdaptation = Optional.empty();

   public DelegatingElementBuilderImpl(StructurePoolElement delegate) {
      this.delegate = delegate;
   }

   @Override
   public DelegatingElementBuilder forcedCount(int count) {
      if (this.maxCount.isPresent()) {
         throw new IllegalStateException("Forced count and max count cannot both be present.");
      } else {
         this.forcedCount = Optional.of(count);
         return this;
      }
   }

   @Override
   public DelegatingElementBuilder limitedCount(int count) {
      if (this.forcedCount.isPresent()) {
         throw new IllegalStateException("Forced count and max count cannot both be present.");
      } else {
         this.maxCount = Optional.of(count);
         return this;
      }
   }

   @Override
   public DelegatingElementBuilder allowedDepth(InclusiveRange<Integer> allowedDepth) {
      this.allowedDepth = Optional.of(allowedDepth);
      return this;
   }

   @Override
   public DelegatingElementBuilder terrainAdaptation(TerrainAdjustment adaptation) {
      this.overrideTerrainAdaptation = Optional.of(adaptation);
      return this;
   }

   @Override
   public DelegatingElementBuilder condition(PlacementCondition condition) {
      this.placementCondition = Optional.of(condition);
      return this;
   }

   @Override
   public DelegatingElementBuilder named(ResourceLocation name) {
      this.name = Optional.of(name);
      return this;
   }

   @Override
   public DelegatingElementBuilder terrainMatchingHeightmap(Types heightmap) {
      this.terrainMatchingHeightmap = heightmap;
      return this;
   }

   @Override
   public DelegatingElementBuilder allowBoundingBoxCollisions() {
      this.allowBoundingBoxCollisions = true;
      return this;
   }

   @Override
   public DelegatingElementBuilder otherPiecesCanIntersect() {
      this.otherPiecesCanIntersect = true;
      return this;
   }

   @Override
   public DelegatingConfig build() {
      return new DelegatingConfig(
         this.delegate,
         this.name,
         this.placementCondition,
         this.allowedDepth,
         this.forcedCount,
         this.maxCount,
         this.allowBoundingBoxCollisions,
         this.otherPiecesCanIntersect,
         this.terrainMatchingHeightmap,
         this.overrideTerrainAdaptation
      );
   }
}
