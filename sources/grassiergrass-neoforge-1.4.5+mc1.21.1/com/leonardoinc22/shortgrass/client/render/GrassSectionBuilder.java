package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;

final class GrassSectionBuilder {
   private static final float SNOW_TIP_START = 0.42F;
   private static final float SNOW_TIP_END = 0.9F;
   private static final float FULL_SNOW_MAX_LENGTH = 0.58F;
   private static final float FULL_SNOW_FADE_END = 0.75F;
   private static final float GREEN_PATCH_SNOW_STRENGTH = 0.62F;
   private static final float PURE_WHITE_NOISE_START = 0.12F;
   private static final float PURE_WHITE_NOISE_END = 0.38F;
   private static final int SNOW_TIP_TINT = 16777215;
   private static final RandomSource BLADE_RANDOM = RandomSource.create();
   private static final Block GRASS_SLAB = (Block)BuiltInRegistries.BLOCK
      .getOptional(ResourceLocation.fromNamespaceAndPath("terrain_slabs", "grass_slab"))
      .orElse(null);
   private static final int BLOCK_STRIDE = 8;
   private static final RandomSource PLANT_RANDOM = RandomSource.create();

   private GrassSectionBuilder() {
   }

   static GrassSectionMesh prepareMesh(long key, long now, boolean computeMode, Vec3 cameraPos) {
      int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
      int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
      int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
      GrassSectionMesh mesh = new GrassSectionMesh();
      mesh.bounds = GrassDrawDispatcher.sectionBounds(originX, originY, originZ);
      mesh.builtAtTick = now;
      int tier = GrassGeometry.lodTier(horizontalSectionDistanceSqr(originX, originZ, cameraPos), GrassConfig.renderRadius);
      mesh.lodTier = tier;
      mesh.anim = computeMode && tier > 0 ? GrassSectionMesh.Anim.BAKE_PENDING : GrassSectionMesh.Anim.ANIMATED;
      return mesh;
   }

   static void emit(GrassSectionBuildBuffers buffers, BlockAndTintGetter region, ClientLevel climateLevel, long key, int tier) {
      int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
      int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
      int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
      emitSection(buffers, region, climateLevel, originX, originY, originZ, tier);
   }

   static boolean sectionMayHaveContent(ClientLevel level, long key) {
      int sectionX = SectionPos.x(key);
      int sectionY = SectionPos.y(key);
      int sectionZ = SectionPos.z(key);
      LevelChunk chunk = level.getChunk(sectionX, sectionZ);
      int index = level.getSectionIndexFromSectionY(sectionY);
      LevelChunkSection[] sections = chunk.getSections();
      if (index >= 0 && index < sections.length) {
         LevelChunkSection section = sections[index];
         return !section.hasOnlyAir() && section.maybeHas(GrassGeometry.RENDERS_CONTENT);
      } else {
         return false;
      }
   }

   private static void emitSection(
      GrassSectionBuildBuffers buffers, BlockAndTintGetter level, ClientLevel climateLevel, int originX, int originY, int originZ, int tier
   ) {
      float configuredBladeHeight = GrassConfig.bladeHeight;
      float grassSparsity = GrassConfig.grassSparsity();
      float bladeHueJitterDegrees = GrassConfig.bladeHueJitterDegrees();
      int baseBladesPerBlock = GrassGeometry.lodBladesPerBlock(GrassConfig.bladesPerBlock, tier);
      int segments = GrassGeometry.LOD_SEGMENTS[tier];
      GrassConfig.GrassStyle style = GrassConfig.grassStyle;
      BlockColors blockColors = Minecraft.getInstance().getBlockColors();
      MutableBlockPos pos = new MutableBlockPos();
      MutableBlockPos above = new MutableBlockPos();
      MutableBlockPos below = new MutableBlockPos();

      for (int dx = 0; dx < 16; dx++) {
         for (int dz = 0; dz < 16; dz++) {
            for (int dy = 0; dy < 16; dy++) {
               int worldX = originX + dx;
               int worldY = originY + dy;
               int worldZ = originZ + dz;
               pos.set(worldX, worldY, worldZ);
               BlockState state = level.getBlockState(pos);
               if (!GrassConfig.isPlantBlacklisted(state.getBlock())) {
                  BlockState tintState = state;
                  float baseOffset;
                  float heightMultiplier;
                  int heightClass;
                  boolean lightAbove;
                  BlockState irisBlockState;
                  int irisLocalY;
                  if (state.is(Blocks.GRASS_BLOCK)) {
                     above.set(worldX, worldY + 1, worldZ);
                     BlockState aboveState = level.getBlockState(above);
                     boolean grassThroughSnow = GrassConfig.grassThroughSnow && aboveState.is(Blocks.SNOW);
                     boolean convertedShortGrass = isConvertedShortGrass(aboveState);
                     if (state.getOptionalValue(SnowyDirtBlock.SNOWY).orElse(false) && !grassThroughSnow
                        || !aboveState.isAir() && !grassThroughSnow && !convertedShortGrass) {
                        continue;
                     }

                     baseOffset = 1.0F;
                     heightMultiplier = 1.0F;
                     heightClass = 0;
                     lightAbove = true;
                     irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                     irisLocalY = dy + 1;
                  } else if (GRASS_SLAB != null && state.is(GRASS_SLAB)) {
                     above.set(worldX, worldY + 1, worldZ);
                     BlockState aboveState = level.getBlockState(above);
                     boolean grassThroughSnow = GrassConfig.grassThroughSnow && aboveState.is(Blocks.SNOW);
                     boolean convertedShortGrass = isConvertedShortGrass(aboveState);
                     if (state.getOptionalValue(SnowyDirtBlock.SNOWY).orElse(false) && !grassThroughSnow
                        || !aboveState.isAir() && !grassThroughSnow && !convertedShortGrass) {
                        continue;
                     }

                     baseOffset = state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM ? 0.5F : 1.0F;
                     heightMultiplier = 1.0F;
                     heightClass = 0;
                     lightAbove = true;
                     irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                     irisLocalY = dy + 1;
                  } else if (isConvertedShortGrass(state)) {
                     below.set(worldX, worldY - 1, worldZ);
                     BlockState belowState = level.getBlockState(below);
                     if (belowState.is(Blocks.GRASS_BLOCK) || GRASS_SLAB != null && belowState.is(GRASS_SLAB)) {
                        continue;
                     }

                     baseOffset = 0.0F;
                     heightMultiplier = 1.0F;
                     heightClass = 0;
                     lightAbove = false;
                     irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                     irisLocalY = dy;
                  } else if (HiddenGrass.isBladeGrassPlant(state)
                     && state.is(Blocks.TALL_GRASS)
                     && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                     baseOffset = 0.0F;
                     heightMultiplier = 3.0F;
                     heightClass = 2;
                     lightAbove = false;
                     irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                     irisLocalY = dy;
                  } else {
                     if (!GrassConfig.isPlantWhitelisted(state.getBlock())) {
                        if (!HiddenGrass.isSwayingPlant(state)) {
                           continue;
                        }

                        ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                        BakedModel original = HiddenGrass.originalModel(mrl);
                        if (original == null) {
                           continue;
                        }

                        int plantLight = LevelRenderer.getLightColor(level, pos);
                        buffers.beginPlantLightRun(dx, dy, dz);
                        float plantNoiseX = GrassGeometry.worldNoiseCoord(worldX + 0.5F);
                        float plantNoiseZ = GrassGeometry.worldNoiseCoord(worldZ + 0.5F);
                        float baseFraction = 0.0F;
                        float topFraction = 1.0F;
                        if (state.getBlock() instanceof DoublePlantBlock) {
                           boolean lower = state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
                           baseFraction = lower ? 0.0F : 0.5F;
                           topFraction = lower ? 0.5F : 1.0F;
                        }

                        buffers.beginPlantIrisBlock(state, dx, dy, dz);

                        try {
                           Vec3 base = state.getOffset(level, pos);
                           emitPlant(
                              buffers,
                              original,
                              level,
                              pos,
                              state,
                              blockColors,
                              dx,
                              dy,
                              dz,
                              plantLight,
                              plantNoiseX,
                              plantNoiseZ,
                              baseFraction,
                              topFraction,
                              base
                           );
                           if (!GrassConfig.denseFlowers || !(state.getBlock() instanceof FlowerBlock)) {
                              continue;
                           }

                           List<Vec3> extras = new ArrayList<>();
                           FlowerDensity.collectExtras(level, pos, base, extras);

                           for (Vec3 extra : extras) {
                              emitPlant(
                                 buffers,
                                 original,
                                 level,
                                 pos,
                                 state,
                                 blockColors,
                                 dx,
                                 dy,
                                 dz,
                                 plantLight,
                                 plantNoiseX,
                                 plantNoiseZ,
                                 baseFraction,
                                 topFraction,
                                 extra
                              );
                           }
                           continue;
                        } finally {
                           buffers.endPlantIrisBlock();
                           buffers.finishPlantLightRun();
                        }
                     }

                     baseOffset = 0.0F;
                     heightMultiplier = 1.0F;
                     heightClass = 1;
                     lightAbove = false;
                     irisBlockState = Blocks.GRASS_BLOCK.defaultBlockState();
                     irisLocalY = dy;
                     tintState = Blocks.GRASS_BLOCK.defaultBlockState();
                  }

                  if (!lightAbove) {
                     below.set(worldX, worldY - 1, worldZ);
                     BlockState belowState = level.getBlockState(below);
                     if (GRASS_SLAB != null && belowState.is(GRASS_SLAB) && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
                        baseOffset -= 0.5F;
                     }
                  }

                  int sampleDy = lightAbove ? dy + 1 : dy;
                  int light = LevelRenderer.getLightColor(level, lightAbove ? above : pos);
                  float height = configuredBladeHeight * heightMultiplier;
                  int tint = blockColors.getColor(tintState, level, pos, 0);
                  boolean snowyClimate = ((Biome)climateLevel.getBiome(pos).value()).getPrecipitationAt(pos) == Precipitation.SNOW;
                  buffers.beginBladeLightRun(dx, sampleDy, dz);
                  buffers.beginBladeIrisBlock(irisBlockState, dx, irisLocalY, dz, GrassGeometry.segmentBandScale(heightMultiplier));

                  try {
                     emitBlock(
                        buffers,
                        worldX,
                        worldY,
                        worldZ,
                        dx,
                        dy,
                        dz,
                        light,
                        height,
                        tint,
                        baseBladesPerBlock,
                        style,
                        segments,
                        baseOffset,
                        heightClass,
                        grassSparsity,
                        bladeHueJitterDegrees,
                        snowyClimate
                     );
                  } finally {
                     buffers.endBladeIrisBlock();
                     buffers.finishBladeLightRun();
                  }
               }
            }
         }
      }
   }

   private static boolean isConvertedShortGrass(BlockState state) {
      return state.is(Blocks.SHORT_GRASS) && HiddenGrass.isBladeGrassPlant(state) && !GrassConfig.isPlantBlacklisted(state.getBlock());
   }

   private static double horizontalSectionDistanceSqr(int originX, int originZ, Vec3 cameraPos) {
      double dx = originX + 8.0 - cameraPos.x;
      double dz = originZ + 8.0 - cameraPos.z;
      return dx * dx + dz * dz;
   }

   private static void emitBlock(
      GrassSectionBuildBuffers buffers,
      int worldX,
      int worldY,
      int worldZ,
      int localX,
      int localY,
      int localZ,
      int light,
      float configuredHeight,
      int tint,
      int bladesPerBlock,
      GrassConfig.GrassStyle style,
      int segments,
      float baseOffset,
      int heightClass,
      float grassSparsity,
      float bladeHueJitterDegrees,
      boolean snowyClimate
   ) {
      long seed = BlockPos.asLong(worldX, worldY, worldZ);
      BLADE_RANDOM.setSeed(seed);
      float baseLocalY = localY + baseOffset;

      for (int blade = 0; blade < bladesPerBlock; blade++) {
         float bx = 0.08F + BLADE_RANDOM.nextFloat();
         float bz = 0.08F + BLADE_RANDOM.nextFloat();
         float angle = quantizedBladeAngle(BLADE_RANDOM.nextFloat() * 6.2831855F);
         float cx = localX + bx;
         float cz = localZ + bz;
         float dirX = Mth.cos(angle);
         float dirZ = Mth.sin(angle);
         float noiseX = GrassGeometry.worldNoiseCoord(worldX + bx);
         float noiseZ = GrassGeometry.worldNoiseCoord(worldZ + bz);
         if (!(grassSparsity > 0.0F) || GrassClumpField.keepBlade(noiseX, noiseZ, grassSparsity, bladeSparsityRandom(worldX, worldY, worldZ, blade))) {
            if (style == GrassConfig.GrassStyle.SEGMENTED) {
               buffers.setBladeBandColumn(GrassGeometry.segmentBandColumnU(worldX, worldZ, blade));
            }

            int bladeTint = GrassHueNoise.shiftHue(tint, worldX + bx, worldZ + bz, angle / 6.2831855F, bladeHueJitterDegrees);
            float bladeLengthMultiplier = snowyClimate ? GrassClumpField.bladeLengthMultiplier(noiseX, noiseZ, angle, heightClass) : 1.0F;
            float snowCoverage = snowyClimate ? GrassClumpField.snowCoverage(noiseX, noiseZ) : 0.5F;
            float tipY = baseLocalY + configuredHeight * (style == GrassConfig.GrassStyle.SEGMENTED ? 1.45F : 1.45F);
            if (segments >= 3) {
               float prevY = baseLocalY;
               float prevT = 0.0F;
               float prevWidth = GrassGeometry.bladeWidthAt(0.0F, style);

               for (int section = 1; section <= segments; section++) {
                  float t = (float)section / segments;
                  float y = Mth.lerp(t, baseLocalY, tipY);
                  float width = GrassGeometry.bladeWidthAt(t, style);
                  emitBladeSection(
                     buffers,
                     cx,
                     cz,
                     dirX,
                     dirZ,
                     angle,
                     noiseX,
                     noiseZ,
                     light,
                     bladeTint,
                     snowyClimate,
                     bladeLengthMultiplier,
                     snowCoverage,
                     prevY,
                     prevT,
                     prevWidth,
                     y,
                     t,
                     width,
                     heightClass
                  );
                  prevY = y;
                  prevT = t;
                  prevWidth = width;
               }
            } else if (segments == 2) {
               float splitY = Mth.lerp(0.6666667F, baseLocalY, tipY);
               emitBladeSection(
                  buffers,
                  cx,
                  cz,
                  dirX,
                  dirZ,
                  angle,
                  noiseX,
                  noiseZ,
                  light,
                  bladeTint,
                  snowyClimate,
                  bladeLengthMultiplier,
                  snowCoverage,
                  baseLocalY,
                  0.0F,
                  GrassGeometry.bladeWidthAt(0.0F, style),
                  splitY,
                  0.6666667F,
                  GrassGeometry.bladeWidthAt(0.6666667F, style),
                  heightClass
               );
               emitBladeSection(
                  buffers,
                  cx,
                  cz,
                  dirX,
                  dirZ,
                  angle,
                  noiseX,
                  noiseZ,
                  light,
                  bladeTint,
                  snowyClimate,
                  bladeLengthMultiplier,
                  snowCoverage,
                  splitY,
                  0.6666667F,
                  GrassGeometry.bladeWidthAt(0.6666667F, style),
                  tipY,
                  1.0F,
                  GrassGeometry.bladeWidthAt(1.0F, style),
                  heightClass
               );
            } else {
               emitBladeSection(
                  buffers,
                  cx,
                  cz,
                  dirX,
                  dirZ,
                  angle,
                  noiseX,
                  noiseZ,
                  light,
                  bladeTint,
                  snowyClimate,
                  bladeLengthMultiplier,
                  snowCoverage,
                  baseLocalY,
                  0.0F,
                  GrassGeometry.bladeWidthAt(0.0F, style),
                  tipY,
                  1.0F,
                  GrassGeometry.bladeWidthAt(1.0F, style),
                  heightClass
               );
            }
         }
      }
   }

   private static float bladeSparsityRandom(int worldX, int worldY, int worldZ, int blade) {
      long hash = BlockPos.asLong(worldX, worldY, worldZ) ^ blade * -7046029254386353131L;
      hash = (hash ^ hash >>> 30) * -4658895280553007687L;
      hash = (hash ^ hash >>> 27) * -7723592293110705685L;
      hash ^= hash >>> 31;
      return (float)(hash >>> 40) * 5.9604645E-8F;
   }

   private static void emitBladeSection(
      GrassSectionBuildBuffers buffers,
      float cx,
      float cz,
      float dirX,
      float dirZ,
      float angle,
      float noiseX,
      float noiseZ,
      int light,
      int tint,
      boolean snowyClimate,
      float bladeLengthMultiplier,
      float snowCoverage,
      float lowerY,
      float lowerT,
      float lowerWidth,
      float upperY,
      float upperT,
      float upperWidth,
      int heightClass
   ) {
      float lowerSideX = dirX * lowerWidth;
      float lowerSideZ = dirZ * lowerWidth;
      float upperSideX = dirX * upperWidth;
      float upperSideZ = dirZ * upperWidth;
      float lowerLeftX = cx - lowerSideX;
      float lowerLeftZ = cz - lowerSideZ;
      float lowerRightX = cx + lowerSideX;
      float lowerRightZ = cz + lowerSideZ;
      float upperLeftX = cx - upperSideX;
      float upperLeftZ = cz - upperSideZ;
      float upperRightX = cx + upperSideX;
      float upperRightZ = cz + upperSideZ;
      float colorBrightness = buffers.bladeColorBrightness();
      float lowerSnow = snowBlend(snowyClimate, bladeLengthMultiplier, snowCoverage, lowerT);
      float upperSnow = snowBlend(snowyClimate, bladeLengthMultiplier, snowCoverage, upperT);
      boolean snowSurface = lowerSnow + upperSnow >= 1.0F;
      int lowerTint = bladeTint(tint, lowerSnow, colorBrightness);
      int upperTint = bladeTint(tint, upperSnow, colorBrightness);
      buffers.bladeVertex(lowerLeftX, lowerY, lowerLeftZ, 0.0F, lowerT, -1.0F, angle, noiseX, noiseZ, light, lowerTint, heightClass, snowSurface, lowerSnow);
      buffers.bladeVertex(lowerRightX, lowerY, lowerRightZ, 1.0F, lowerT, 1.0F, angle, noiseX, noiseZ, light, lowerTint, heightClass, snowSurface, lowerSnow);
      buffers.bladeVertex(upperRightX, upperY, upperRightZ, 1.0F, upperT, 1.0F, angle, noiseX, noiseZ, light, upperTint, heightClass, snowSurface, upperSnow);
      buffers.bladeVertex(upperLeftX, upperY, upperLeftZ, 0.0F, upperT, -1.0F, angle, noiseX, noiseZ, light, upperTint, heightClass, snowSurface, upperSnow);
   }

   private static float snowBlend(boolean snowyClimate, float bladeLengthMultiplier, float snowCoverage, float t) {
      if (!snowyClimate) {
         return 0.0F;
      } else {
         float coverageOffset = snowCoverage - 0.5F;
         float tipStart = 0.42F - coverageOffset * 0.24F;
         float tipEnd = 0.9F - coverageOffset * 0.1F;
         float blend = Mth.clamp((t - tipStart) / (tipEnd - tipStart), 0.0F, 1.0F);
         blend = blend * blend * (3.0F - 2.0F * blend);
         float fullSnowLength = 0.58F + coverageOffset * 0.18F;
         float fullSnowFadeEnd = 0.75F + coverageOffset * 0.14F;
         float shortBladeBlend = 1.0F - Mth.clamp((bladeLengthMultiplier - fullSnowLength) / (fullSnowFadeEnd - fullSnowLength), 0.0F, 1.0F);
         shortBladeBlend = shortBladeBlend * shortBladeBlend * (3.0F - 2.0F * shortBladeBlend);
         float pureWhite = Mth.clamp((snowCoverage - 0.12F) / 0.26F, 0.0F, 1.0F);
         pureWhite = pureWhite * pureWhite * (3.0F - 2.0F * pureWhite);
         float patchStrength = Mth.lerp(pureWhite, 0.62F, 1.0F);
         return Math.max(blend, shortBladeBlend) * patchStrength;
      }
   }

   private static int bladeTint(int tint, float blend, float colorBrightness) {
      int red = Math.round(Mth.lerp(blend, Mth.clamp((tint >> 16 & 0xFF) * colorBrightness, 0.0F, 255.0F), 255.0F));
      int green = Math.round(Mth.lerp(blend, Mth.clamp((tint >> 8 & 0xFF) * colorBrightness, 0.0F, 255.0F), 255.0F));
      int blue = Math.round(Mth.lerp(blend, Mth.clamp((tint & 0xFF) * colorBrightness, 0.0F, 255.0F), 255.0F));
      return red << 16 | green << 8 | blue;
   }

   private static void emitPlant(
      GrassSectionBuildBuffers buffers,
      BakedModel model,
      BlockAndTintGetter level,
      BlockPos pos,
      BlockState state,
      BlockColors blockColors,
      int localX,
      int localY,
      int localZ,
      int light,
      float noiseX,
      float noiseZ,
      float baseFraction,
      float topFraction,
      Vec3 offset
   ) {
      float normalX = noiseX * 2.0F - 1.0F;
      float normalZ = noiseZ * 2.0F - 1.0F;
      float baseX = localX + (float)offset.x;
      float baseY = localY + (float)offset.y;
      float baseZ = localZ + (float)offset.z;
      long seed = pos.asLong();
      PLANT_RANDOM.setSeed(seed);
      emitPlantQuads(
         buffers,
         model.getQuads(state, null, PLANT_RANDOM),
         level,
         pos,
         state,
         blockColors,
         baseX,
         baseY,
         baseZ,
         light,
         normalX,
         normalZ,
         baseFraction,
         topFraction
      );

      for (Direction direction : Direction.values()) {
         PLANT_RANDOM.setSeed(seed);
         emitPlantQuads(
            buffers,
            model.getQuads(state, direction, PLANT_RANDOM),
            level,
            pos,
            state,
            blockColors,
            baseX,
            baseY,
            baseZ,
            light,
            normalX,
            normalZ,
            baseFraction,
            topFraction
         );
      }
   }

   private static void emitPlantQuads(
      GrassSectionBuildBuffers buffers,
      List<BakedQuad> quads,
      BlockAndTintGetter level,
      BlockPos pos,
      BlockState state,
      BlockColors blockColors,
      float baseX,
      float baseY,
      float baseZ,
      int light,
      float normalX,
      float normalZ,
      float baseFraction,
      float topFraction
   ) {
      for (BakedQuad quad : quads) {
         int tintIndex = quad.getTintIndex();
         int tint = tintIndex == -1 ? -1 : blockColors.getColor(state, level, pos, tintIndex);
         int red = tint >> 16 & 0xFF;
         int green = tint >> 8 & 0xFF;
         int blue = tint & 0xFF;
         int[] verts = quad.getVertices();

         for (int vertex = 0; vertex < 4; vertex++) {
            int o = vertex * 8;
            float x = Float.intBitsToFloat(verts[o]);
            float y = Float.intBitsToFloat(verts[o + 1]);
            float z = Float.intBitsToFloat(verts[o + 2]);
            float u = atlasU(quad, Float.intBitsToFloat(verts[o + 4]));
            float v = atlasV(quad, Float.intBitsToFloat(verts[o + 5]));
            float heightFraction = baseFraction + Mth.clamp(y, 0.0F, 1.0F) * (topFraction - baseFraction);
            buffers.plantVertex(baseX + x, baseY + y, baseZ + z, u, v, heightFraction, normalX, normalZ, light, red, green, blue);
         }
      }
   }

   private static float atlasU(BakedQuad quad, float u) {
      return isSpriteRelativeUv(u) ? quad.getSprite().getU(u / 16.0F) : u;
   }

   private static float atlasV(BakedQuad quad, float v) {
      return isSpriteRelativeUv(v) ? quad.getSprite().getV(v / 16.0F) : v;
   }

   private static boolean isSpriteRelativeUv(float uv) {
      return uv < 0.0F || uv > 1.0F;
   }

   private static float quantizedBladeAngle(float angle) {
      int angleBucket = Mth.clamp((int)(positiveAngle(angle) / 6.2831855F * 31.0F), 0, 31);
      return angleBucket / 31.0F * 6.2831855F;
   }

   private static float positiveAngle(float angle) {
      return angle - Mth.floor(angle / 6.2831855F) * 6.2831855F;
   }
}
