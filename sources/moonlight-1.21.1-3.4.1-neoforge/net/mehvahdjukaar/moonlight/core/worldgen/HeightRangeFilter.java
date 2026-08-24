package net.mehvahdjukaar.moonlight.core.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.misc.CodecMapRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HeightRangeFilter extends PlacementFilter {
   public static final MapCodec<HeightRangeFilter> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            HeightRangeFilter.VerticalProvider.CODEC.listOf().optionalFieldOf("below", List.of()).forGetter(p -> p.below),
            HeightRangeFilter.VerticalProvider.CODEC.listOf().optionalFieldOf("above", List.of()).forGetter(p -> p.above)
         )
         .apply(i, HeightRangeFilter::new)
   );
   private final List<HeightRangeFilter.VerticalProvider> below;
   private final List<HeightRangeFilter.VerticalProvider> above;

   private HeightRangeFilter(List<HeightRangeFilter.VerticalProvider> below, List<HeightRangeFilter.VerticalProvider> above) {
      this.below = below;
      this.above = above;
   }

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      for (HeightRangeFilter.VerticalProvider provider : this.below) {
         int limitY = provider.resolveY(context, pos.getX(), pos.getZ());
         if (pos.getY() > limitY) {
            return false;
         }
      }

      for (HeightRangeFilter.VerticalProvider providerx : this.above) {
         int limitY = providerx.resolveY(context, pos.getX(), pos.getZ());
         if (pos.getY() < limitY) {
            return false;
         }
      }

      return true;
   }

   public PlacementModifierType<?> type() {
      return MoonlightRegistry.HEIGHT_RANGE.get();
   }

   public record Anchor(VerticalAnchor anchor) implements HeightRangeFilter.VerticalProvider {
      public static final MapCodec<HeightRangeFilter.Anchor> CODEC = VerticalAnchor.CODEC
         .fieldOf("anchor")
         .xmap(HeightRangeFilter.Anchor::new, HeightRangeFilter.Anchor::anchor);

      @Override
      public int resolveY(PlacementContext context, int x, int z) {
         return this.anchor.resolveY(context);
      }

      @Override
      public String toString() {
         return this.anchor.toString();
      }

      @Override
      public MapCodec<HeightRangeFilter.Anchor> getCodec() {
         return CODEC;
      }
   }

   public record HeightmapAnchor(Types heightmap, int offset) implements HeightRangeFilter.VerticalProvider {
      public static final MapCodec<HeightRangeFilter.HeightmapAnchor> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Types.CODEC.fieldOf("heightmap").forGetter(HeightRangeFilter.HeightmapAnchor::heightmap),
               Codec.INT.fieldOf("offset").forGetter(HeightRangeFilter.HeightmapAnchor::offset)
            )
            .apply(instance, HeightRangeFilter.HeightmapAnchor::new)
      );

      @Override
      public int resolveY(PlacementContext context, int x, int z) {
         return context.getHeight(this.heightmap, x, z) + this.offset;
      }

      @Override
      public String toString() {
         return this.offset + " from heightmap " + this.heightmap;
      }

      @Override
      public MapCodec<HeightRangeFilter.HeightmapAnchor> getCodec() {
         return CODEC;
      }
   }

   public record SeaLevelAnchor(int offset) implements HeightRangeFilter.VerticalProvider {
      public static final MapCodec<HeightRangeFilter.SeaLevelAnchor> CODEC = Codec.INT
         .fieldOf("offset")
         .xmap(HeightRangeFilter.SeaLevelAnchor::new, HeightRangeFilter.SeaLevelAnchor::offset);

      @Override
      public int resolveY(PlacementContext context, int x, int z) {
         return context.getLevel().getSeaLevel() + this.offset;
      }

      @Override
      public String toString() {
         return this.offset + " from sea level";
      }

      @Override
      public MapCodec<HeightRangeFilter.SeaLevelAnchor> getCodec() {
         return CODEC;
      }
   }

   private interface VerticalProvider {
      CodecMapRegistry<HeightRangeFilter.VerticalProvider> REG = (CodecMapRegistry<HeightRangeFilter.VerticalProvider>)Util.make(() -> {
         CodecMapRegistry<HeightRangeFilter.VerticalProvider> reg = new CodecMapRegistry<>("vertical_providers");
         reg.register("anchor", HeightRangeFilter.Anchor.CODEC);
         reg.register("sea_level", HeightRangeFilter.SeaLevelAnchor.CODEC);
         reg.register("heightmap", HeightRangeFilter.HeightmapAnchor.CODEC);
         return reg;
      });
      Codec<HeightRangeFilter.VerticalProvider> CODEC = REG.dispatch(HeightRangeFilter.VerticalProvider::getCodec);

      int resolveY(PlacementContext var1, int var2, int var3);

      MapCodec<? extends HeightRangeFilter.VerticalProvider> getCodec();
   }
}
