package com.aetherteam.aether.data.resources.builders;

import com.aetherteam.aether.world.placementmodifier.DungeonBlacklistFilter;
import com.aetherteam.aether.world.placementmodifier.ImprovedLayerPlacementModifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

public class AetherPlacedFeatureBuilders {
   public static List<PlacementModifier> aercloudPlacement(int above, int range, int chance) {
      return List.of(
         HeightRangePlacement.uniform(VerticalAnchor.absolute(above), VerticalAnchor.absolute(above + range)),
         RarityFilter.onAverageOnceEvery(chance),
         InSquarePlacement.spread(),
         BiomeFilter.biome(),
         new DungeonBlacklistFilter()
      );
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier count) {
      return treePlacementBase(count).build();
   }

   private static Builder<PlacementModifier> treePlacementBase(PlacementModifier count) {
      return ImmutableList.builder()
         .add(count)
         .add(SurfaceWaterDepthFilter.forMaxDepth(0))
         .add(ImprovedLayerPlacementModifier.of(Types.OCEAN_FLOOR, UniformInt.of(0, 1), 4))
         .add(BiomeFilter.biome())
         .add(new DungeonBlacklistFilter());
   }
}
