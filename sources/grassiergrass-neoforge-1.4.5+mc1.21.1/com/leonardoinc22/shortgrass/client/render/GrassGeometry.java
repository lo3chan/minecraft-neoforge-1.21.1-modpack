package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class GrassGeometry {
   static final float BASE_WIDTH = 0.06F;
   static final float MIDDLE_WIDTH = 0.045F;
   static final float TIP_WIDTH = 0.0075F;
   static final float BLADE_SPLIT_FRACTION = 0.6666667F;
   static final float TIP_EXTENSION_RATIO = 0.45F;
   static final float TALL_PLANT_HEIGHT_MULTIPLIER = 3.0F;
   static final float BLADE_TOP_Y = 1.0F;
   static final float NOISE_WORLD_TILE_BLOCKS = 32.0F;
   static final float SEGMENTED_WIDTH = 0.05F;
   static final float SEGMENTED_HEIGHT_MULTIPLIER = 1.45F;
   static final float TAPERED_WIDTH_SCALE = 0.8333333F;
   static final float[] LOD_RING_FRACTION = new float[]{0.55F, 0.65F};
   static final int[] LOD_SEGMENTS = new int[]{5, 3, 2};
   static final float[] LOD_DENSITY = new float[]{1.0F, 0.7F, 0.15F};
   static final int LOD_TIERS = LOD_SEGMENTS.length;
   static final int ANIMATE_MAX_TIER = 0;
   static final Predicate<BlockState> RENDERS_GRASS = state -> state.is(Blocks.GRASS_BLOCK)
      || HiddenGrass.isBladeGrassPlant(state)
      || GrassConfig.isPlantWhitelisted(state.getBlock());
   static final Predicate<BlockState> RENDERS_CONTENT = state -> RENDERS_GRASS.test(state) || HiddenGrass.isSwayingPlant(state);
   static final float SEGMENT_MIN_BAND_HEIGHT = 0.14F;
   static final float SEGMENT_MAX_COUNT = 4.0F;
   static final int SEGMENT_BAND_VARIANTS = 8;

   static int lodTier(double horizontalDistanceSqr, int renderRadius) {
      double r = renderRadius;

      for (int tier = 0; tier < LOD_RING_FRACTION.length; tier++) {
         double edge = r * LOD_RING_FRACTION[tier];
         if (horizontalDistanceSqr <= edge * edge) {
            return tier;
         }
      }

      return LOD_RING_FRACTION.length;
   }

   static int lodBladesPerBlock(int baseBladesPerBlock, int tier) {
      return Math.max(1, Math.round(baseBladesPerBlock * LOD_DENSITY[tier]));
   }

   private GrassGeometry() {
   }

   static float bladeWidthAt(float t, GrassConfig.GrassStyle style) {
      if (style == GrassConfig.GrassStyle.SEGMENTED) {
         return 0.05F * GrassConfig.bladeWidth;
      } else {
         float taperedWidth = GrassConfig.bladeWidth * 0.8333333F;
         return t <= 0.6666667F
            ? Mth.lerp(t / 0.6666667F, 0.06F, 0.045F) * taperedWidth
            : Mth.lerp((t - 0.6666667F) / 0.3333333F, 0.045F, 0.0075F) * taperedWidth;
      }
   }

   static float visualBladeHeight() {
      return GrassConfig.bladeHeight * (GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED ? 1.45F : 1.45F);
   }

   static float segmentBandColumnU(int worldX, int worldZ, int blade) {
      int h = worldX * 374761393 + worldZ * 668265263 + blade * 1013904223;
      h = (h ^ h >>> 13) * 1274126177;
      int col = Math.floorMod(h, 8);
      return (col + 0.5F) / 8.0F;
   }

   static float segmentBandScale(float heightMultiplier) {
      float length = visualBladeHeight() * heightMultiplier;
      float count = Mth.clamp((float)Math.floor(length / 0.14F), 1.0F, 4.0F);
      return count / 4.0F;
   }

   static float worldNoiseCoord(float worldCoord) {
      return worldCoord / 32.0F - Mth.floor(worldCoord / 32.0F);
   }
}
