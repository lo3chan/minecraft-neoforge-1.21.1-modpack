package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.client.render.iris.IrisCompat;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class GrassRenderPass {
   private static final ResourceLocation BLADE_SPRITE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "block/blade");
   private static final ResourceLocation BANDS_SPRITE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "block/grass_bands");
   private static final ResourceLocation TAPERED_SPRITE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "block/grass_tapered");
   private static ClientLevel cachedLevel;
   private static boolean cachedShaderPackMode;
   private static boolean cachedComputeMode;
   private static GrassConfig.GrassStyle cachedStyle;
   private static float cachedGrassSparsity = 0.0F / 0.0F;
   private static float cachedBladeHueJitterDegrees = 0.0F / 0.0F;
   private static float cachedGrassBrightness = 0.0F / 0.0F;
   private static float cachedHeightVariation = 0.0F / 0.0F;
   private static boolean cachedGrassThroughSnow;
   private static boolean cachedGrassPlantsAsBlades;
   private static float cachedIrisBladeLightBrightness = 0.0F / 0.0F;
   private static float cachedIrisPlantLightBrightness = 0.0F / 0.0F;
   private static float cachedBladeGradientBottom = 0.0F / 0.0F;
   private static float cachedBladeGradientTop = 0.0F / 0.0F;
   private static float cachedBladeGradientCurve = 0.0F / 0.0F;

   private GrassRenderPass() {
   }

   public static void render(Camera camera, float partialTick, Matrix4f projection, Matrix4f modelView, Frustum frustum) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      boolean irisMode = IrisCompat.isShaderPackInUse();
      if (!irisMode || !IrisCompat.isRenderingShadowPass() || GrassConfig.shaderPackShadows) {
         boolean computeMode = irisMode && GrassComputeAnimator.isAvailable();
         GrassConfig.GrassStyle style = GrassConfig.grassStyle;
         float grassSparsity = GrassConfig.grassSparsity();
         float bladeHueJitterDegrees = GrassConfig.bladeHueJitterDegrees();
         float grassBrightness = GrassConfig.grassBrightness;
         float heightVariation = GrassConfig.heightVariation;
         boolean grassThroughSnow = GrassConfig.grassThroughSnow;
         boolean grassPlantsAsBlades = GrassConfig.grassPlantsAsBlades;
         float irisBladeLightBrightness = irisMode ? GrassIrisBrightness.lightBrightness(false) : 0.0F / 0.0F;
         float irisPlantLightBrightness = irisMode ? GrassIrisBrightness.lightBrightness(true) : 0.0F / 0.0F;
         float bladeGradientBottom = GrassConfig.bladeGradientBottom;
         float bladeGradientTop = GrassConfig.bladeGradientTop;
         float bladeGradientCurve = GrassConfig.bladeGradientCurve;
         if (level != cachedLevel
            || irisMode != cachedShaderPackMode
            || computeMode != cachedComputeMode
            || style != cachedStyle
            || Float.compare(grassSparsity, cachedGrassSparsity) != 0
            || Float.compare(bladeHueJitterDegrees, cachedBladeHueJitterDegrees) != 0
            || Float.compare(grassBrightness, cachedGrassBrightness) != 0
            || Float.compare(heightVariation, cachedHeightVariation) != 0
            || grassThroughSnow != cachedGrassThroughSnow
            || grassPlantsAsBlades != cachedGrassPlantsAsBlades
            || Float.compare(irisBladeLightBrightness, cachedIrisBladeLightBrightness) != 0
            || Float.compare(irisPlantLightBrightness, cachedIrisPlantLightBrightness) != 0
            || Float.compare(bladeGradientBottom, cachedBladeGradientBottom) != 0
            || Float.compare(bladeGradientTop, cachedBladeGradientTop) != 0
            || Float.compare(bladeGradientCurve, cachedBladeGradientCurve) != 0) {
            boolean levelChanged = level != cachedLevel;
            GrassSectionCache.disposeAll();
            if (levelChanged) {
               GrassTrailField.reset(true);
               GrassShaderUniforms.resetWindScroll();
            }

            cachedLevel = level;
            cachedShaderPackMode = irisMode;
            cachedComputeMode = computeMode;
            cachedStyle = style;
            cachedGrassSparsity = grassSparsity;
            cachedBladeHueJitterDegrees = bladeHueJitterDegrees;
            cachedGrassBrightness = grassBrightness;
            cachedHeightVariation = heightVariation;
            cachedGrassThroughSnow = grassThroughSnow;
            cachedGrassPlantsAsBlades = grassPlantsAsBlades;
            cachedIrisBladeLightBrightness = irisBladeLightBrightness;
            cachedIrisPlantLightBrightness = irisPlantLightBrightness;
            cachedBladeGradientBottom = bladeGradientBottom;
            cachedBladeGradientTop = bladeGradientTop;
            cachedBladeGradientCurve = bladeGradientCurve;
         }

         if (level != null && (irisMode || GrassRenderType.getGrassShader() != null)) {
            GrassSectionCache.drainPendingDirtySections();
            Vec3 cameraPos = camera.getPosition();
            long now = level.getGameTime();
            GrassWindParticles.tick(level, cameraPos, now);
            ResourceLocation shapeSprite;
            if (GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED) {
               shapeSprite = BANDS_SPRITE;
            } else if (computeMode) {
               shapeSprite = TAPERED_SPRITE;
            } else {
               shapeSprite = BLADE_SPRITE;
            }

            TextureAtlasSprite bladeSprite = irisMode ? (TextureAtlasSprite)minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(shapeSprite) : null;
            TextureAtlasSprite snowBladeSprite = irisMode
               ? (TextureAtlasSprite)minecraft.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(BLADE_SPRITE)
               : null;
            int radiusSections = (GrassConfig.renderRadius + 15) / 16;
            int verticalSections = GrassSectionCache.verticalSectionRadius(radiusSections);
            int camSx = SectionPos.blockToSectionCoord(Mth.floor(cameraPos.x));
            int camSy = SectionPos.blockToSectionCoord(Mth.floor(cameraPos.y));
            int camSz = SectionPos.blockToSectionCoord(Mth.floor(cameraPos.z));
            GrassSectionCache.evictOutOfRange(camSx, camSy, camSz, radiusSections, verticalSections);
            GrassSectionCache.buildBudgeted(
               level, camSx, camSy, camSz, radiusSections, verticalSections, cameraPos, now, irisMode, computeMode, bladeSprite, snowBladeSprite, frustum
            );
            if (irisMode) {
               GrassDrawDispatcher.drawSectionsIris(GrassSectionCache.meshes(), projection, modelView, frustum, level, cameraPos, partialTick, computeMode);
            } else {
               GrassDrawDispatcher.drawSections(GrassSectionCache.meshes(), projection, modelView, frustum, level, cameraPos, partialTick);
            }
         }
      }
   }

   public static void invalidateBlock(ClientLevel level, BlockPos pos) {
      GrassSectionCache.invalidateBlock(level, cachedLevel, pos);
   }

   public static void invalidateLightSection(ClientLevel level, SectionPos sectionPos) {
      GrassSectionCache.invalidateLightSection(level, cachedLevel, sectionPos);
   }

   public static void invalidateRenderSection(ClientLevel level, int sectionX, int sectionY, int sectionZ) {
      GrassSectionCache.invalidateRenderSection(level, cachedLevel, sectionX, sectionY, sectionZ);
   }

   public static void invalidateChunk(ClientLevel level, int chunkX, int chunkZ) {
      GrassSectionCache.invalidateChunk(level, cachedLevel, chunkX, chunkZ);
   }

   public static void flushCache() {
      GrassSectionCache.disposeAll();
   }

   public static void flushAllGeometry() {
      flushCache();
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         minecraft.levelRenderer.allChanged();
      }
   }

   public static void close() {
      GrassSectionCache.disposeAll();
      GrassSectionCache.resetRefreshTracking();
      GrassShaderUniforms.close();
      GrassTrailField.close();
      GrassTrailField.reset(false);
      cachedLevel = null;
      cachedStyle = null;
      cachedGrassSparsity = 0.0F / 0.0F;
      cachedBladeHueJitterDegrees = 0.0F / 0.0F;
      cachedGrassBrightness = 0.0F / 0.0F;
      cachedHeightVariation = 0.0F / 0.0F;
      cachedGrassThroughSnow = false;
      cachedGrassPlantsAsBlades = false;
      cachedIrisBladeLightBrightness = 0.0F / 0.0F;
      cachedIrisPlantLightBrightness = 0.0F / 0.0F;
      cachedBladeGradientBottom = 0.0F / 0.0F;
      cachedBladeGradientTop = 0.0F / 0.0F;
      cachedBladeGradientCurve = 0.0F / 0.0F;
      cachedShaderPackMode = false;
      cachedComputeMode = false;
   }
}
