package net.mehvahdjukaar.moonlight.core.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@Deprecated(
   forRemoval = true
)
public class CaveFilter extends PlacementFilter {
   public static final MapCodec<CaveFilter> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Types.CODEC.listOf().fieldOf("heightmaps").forGetter(p -> p.belowHeightMaps), Codec.BOOL.fieldOf("below_sea_level").forGetter(p -> p.belowSeaLevel)
         )
         .apply(instance, CaveFilter::new)
   );
   private final List<Types> belowHeightMaps;
   private final Boolean belowSeaLevel;

   private CaveFilter(List<Types> types, Boolean belowSeaLevel) {
      this.belowHeightMaps = types;
      this.belowSeaLevel = belowSeaLevel;
   }

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      if (context.getLevel().getChunkSource() instanceof ServerChunkCache serverChunkCache) {
         int y = pos.getY();
         if (this.belowSeaLevel) {
            int sea = serverChunkCache.getGenerator().getSeaLevel();
            if (y > sea) {
               return false;
            }
         }

         for (Types h : this.belowHeightMaps) {
            int k = context.getHeight(h, pos.getX(), pos.getZ());
            if (y > k) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public PlacementModifierType<?> type() {
      return MoonlightRegistry.CAVE_MODIFIER.get();
   }

   public static class Type implements PlacementModifierType<CaveFilter> {
      public MapCodec<CaveFilter> codec() {
         return CaveFilter.CODEC;
      }
   }
}
