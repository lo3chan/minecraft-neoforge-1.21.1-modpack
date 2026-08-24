package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public record DelegatingConfig(
   StructurePoolElement delegate,
   Optional<ResourceLocation> name,
   Optional<PlacementCondition> placementCondition,
   Optional<InclusiveRange<Integer>> allowedDepth,
   Optional<Integer> forcedCount,
   Optional<Integer> maxCount,
   boolean allowBoundingBoxCollisions,
   boolean otherPiecesCanIntersect,
   Types terrainMatchingHeightmap,
   Optional<TerrainAdjustment> overrideTerrainAdaptation
) {
   public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               StructurePoolElement.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
               ResourceLocation.CODEC.optionalFieldOf("name").forGetter(DelegatingConfig::name),
               PlacementCondition.CODEC.optionalFieldOf("condition").forGetter(DelegatingConfig::placementCondition),
               LithostitchedCodecs.INT_RANGE.optionalFieldOf("allowed_depth").forGetter(DelegatingConfig::allowedDepth),
               ExtraCodecs.POSITIVE_INT.optionalFieldOf("forced_count").forGetter(DelegatingConfig::forcedCount),
               ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_count").forGetter(DelegatingConfig::maxCount),
               Codec.BOOL.optionalFieldOf("allow_bounding_box_collisions", false).forGetter(DelegatingConfig::allowBoundingBoxCollisions),
               Codec.BOOL.optionalFieldOf("other_pieces_can_intersect", false).forGetter(DelegatingConfig::otherPiecesCanIntersect),
               Types.CODEC.optionalFieldOf("terrain_matching_heightmap", Types.WORLD_SURFACE_WG).forGetter(DelegatingConfig::terrainMatchingHeightmap),
               TerrainAdjustment.CODEC.optionalFieldOf("override_terrain_adaptation").forGetter(DelegatingConfig::overrideTerrainAdaptation)
            )
            .apply(instance, DelegatingConfig::new)
      )
      .validate(DelegatingConfig::validate);

   public DelegatingConfig(StructurePoolElement delegate) {
      this(
         delegate,
         Optional.empty(),
         Optional.empty(),
         Optional.empty(),
         Optional.empty(),
         Optional.empty(),
         false,
         false,
         Types.WORLD_SURFACE_WG,
         Optional.empty()
      );
   }

   private static DataResult<DelegatingConfig> validate(DelegatingConfig config) {
      return config.forcedCount.isPresent() && config.maxCount.isPresent()
         ? DataResult.error(() -> "forced_count and max_count cannot both be present.")
         : DataResult.success(config);
   }

   public ResourceLocation getName() {
      return this.name.orElseGet(() -> Lithostitched.id("generated/" + this.delegate.hashCode()));
   }

   public boolean shouldCancelPlacement(GenerationContext context, BlockPos pos, int depth, int count) {
      boolean validDepth = this.allowedDepth.<Boolean>map(range -> range.isValueInRange(depth)).orElse(true);
      boolean validCount = this.forcedCount.<Boolean>map(forced -> count < forced).orElse(true) && this.maxCount.<Boolean>map(max -> count < max).orElse(true);
      boolean validCondition = this.placementCondition.<Boolean>map(condition -> condition.test(context, pos)).orElse(true);
      return !validDepth || !validCount || !validCondition;
   }
}
