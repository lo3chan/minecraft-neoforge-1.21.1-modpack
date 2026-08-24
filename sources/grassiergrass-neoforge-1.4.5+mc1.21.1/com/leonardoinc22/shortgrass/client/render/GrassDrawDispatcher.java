package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.slf4j.Logger;

final class GrassDrawDispatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation NOISE_TEXTURE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "textures/effect/noise.png");
   private static final ResourceLocation BANDS_TEXTURE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "textures/block/grass_bands.png");
   private static final ResourceLocation TAPERED_TEXTURE = ResourceLocation.fromNamespaceAndPath("grassiergrass", "textures/block/grass_tapered.png");
   private static final Matrix4f MODEL_VIEW_SCRATCH = new Matrix4f();
   private static int configuredNoiseTextureId = -1;
   private static int configuredShapeTextureId = -1;
   private static boolean loggedFirstIrisComputeFrame;
   private static final int STATIC_BAKE_BUDGET_PER_FRAME = 8;

   private GrassDrawDispatcher() {
   }

   static void drawSections(
      Long2ObjectOpenHashMap<GrassSectionMesh> meshes,
      Matrix4f projection,
      Matrix4f modelView,
      Frustum frustum,
      ClientLevel level,
      Vec3 cameraPos,
      float partialTick
   ) {
      GrassTrailField.update(level, cameraPos, partialTick);
      float windTime = GrassShaderUniforms.advanceRenderAnimationTicks();
      ShaderInstance shader = GrassRenderType.getGrassShader();
      GrassShaderUniforms.updateAnimationUniforms(shader, windTime, cameraPos);
      shader.safeGetUniform("GrassBrightness").set(1.0F);
      GrassRenderType.grassBlades().setupRenderState();
      configureNoiseTexture();
      boolean segmented = GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED;
      ResourceLocation shapeTexture = segmented ? BANDS_TEXTURE : TAPERED_TEXTURE;
      configureShapeTexture(shapeTexture);
      RenderSystem.setShaderTexture(3, NOISE_TEXTURE);
      RenderSystem.setShaderTexture(4, GrassTrailField.textureLocation());
      RenderSystem.setShaderTexture(5, shapeTexture);
      boolean drewAnything = false;
      ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(meshes);

      while (iterator.hasNext()) {
         Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
         GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
         VertexBuffer buffer = mesh.buffer;
         if (buffer != null) {
            long key = entry.getLongKey();
            int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
            int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
            int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
            if (frustum == null || frustum.isVisible(mesh.bounds)) {
               shader.safeGetUniform("SectionOffset").set((float)(originX - cameraPos.x), (float)(originY - cameraPos.y), (float)(originZ - cameraPos.z));
               buffer.bind();
               buffer.drawWithShader(modelView, projection, shader);
               drewAnything = true;
            }
         }
      }

      if (drewAnything) {
         VertexBuffer.unbind();
      }

      GrassRenderType.grassBlades().clearRenderState();
      drawPlantSections(meshes, projection, modelView, frustum, cameraPos, windTime);
   }

   private static void drawPlantSections(
      Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f modelView, Frustum frustum, Vec3 cameraPos, float windTime
   ) {
      ShaderInstance plantShader = GrassRenderType.getPlantShader();
      if (plantShader != null) {
         GrassShaderUniforms.setSharedWindUniforms(plantShader, windTime, cameraPos);
         GrassRenderType.grassPlants().setupRenderState();
         configureNoiseTexture();
         RenderSystem.setShaderTexture(3, NOISE_TEXTURE);
         RenderSystem.setShaderTexture(4, GrassTrailField.textureLocation());
         boolean drewAnything = false;
         ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(meshes);

         while (iterator.hasNext()) {
            Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            VertexBuffer buffer = mesh.plantBuffer;
            if (buffer != null) {
               long key = entry.getLongKey();
               int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
               int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
               int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
               if (frustum == null || frustum.isVisible(mesh.bounds)) {
                  plantShader.safeGetUniform("SectionOffset")
                     .set((float)(originX - cameraPos.x), (float)(originY - cameraPos.y), (float)(originZ - cameraPos.z));
                  buffer.bind();
                  buffer.drawWithShader(modelView, projection, plantShader);
                  drewAnything = true;
               }
            }
         }

         if (drewAnything) {
            VertexBuffer.unbind();
         }

         GrassRenderType.grassPlants().clearRenderState();
      }
   }

   static void drawSectionsIris(
      Long2ObjectOpenHashMap<GrassSectionMesh> meshes,
      Matrix4f projection,
      Matrix4f baseModelView,
      Frustum frustum,
      ClientLevel level,
      Vec3 cameraPos,
      float partialTick,
      boolean computeMode
   ) {
      ShaderInstance shader = GameRenderer.getRendertypeCutoutShader();
      if (shader != null) {
         GrassTrailField.update(level, cameraPos, partialTick);
         float windTime = GrassShaderUniforms.advanceRenderAnimationTicks();
         GrassShaderUniforms.updateFrameState(windTime, cameraPos);
         if (computeMode) {
            animateSectionsCompute(meshes, frustum, cameraPos);
         }

         drawCachedIris(GrassRenderType.irisTerrainBlades(), shader, meshes, projection, baseModelView, frustum, cameraPos, false);
         drawPlantSectionsIris(meshes, projection, baseModelView, frustum, cameraPos);
      }
   }

   private static void animateSectionsCompute(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Frustum frustum, Vec3 cameraPos) {
      configureNoiseTexture();
      int noiseTextureId = Minecraft.getInstance().getTextureManager().getTexture(NOISE_TEXTURE).getId();
      int trailTextureId = GrassTrailField.textureId();
      GrassComputeAnimator.begin(noiseTextureId, trailTextureId, false);
      int meshCount = 0;
      int computeMeshCount = 0;
      int dispatchCount = 0;
      ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(meshes);

      while (iterator.hasNext()) {
         Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
         GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
         meshCount++;
         if (mesh.anim == GrassSectionMesh.Anim.ANIMATED && mesh.hasComputeBuffers()) {
            computeMeshCount++;
            long key = entry.getLongKey();
            if (frustum == null || frustum.isVisible(mesh.bounds)) {
               float offX = (float)(SectionPos.sectionToBlockCoord(SectionPos.x(key)) - cameraPos.x);
               float offY = (float)(SectionPos.sectionToBlockCoord(SectionPos.y(key)) - cameraPos.y);
               float offZ = (float)(SectionPos.sectionToBlockCoord(SectionPos.z(key)) - cameraPos.z);
               if (mesh.bladeInputGl != 0 && mesh.bladeBufferAlt != null) {
                  GrassComputeAnimator.computeInto(
                     mesh.bladeInputGl,
                     VboHandleAccess.vboId(mesh.bladeBufferAlt),
                     mesh.bladeComputeLayout,
                     mesh.bladeVertexCount,
                     false,
                     offX,
                     offY,
                     offZ,
                     false
                  );
                  dispatchCount++;
               }

               if (mesh.plantInputGl != 0 && mesh.plantBufferAlt != null) {
                  GrassComputeAnimator.computeInto(
                     mesh.plantInputGl,
                     VboHandleAccess.vboId(mesh.plantBufferAlt),
                     mesh.plantComputeLayout,
                     mesh.plantVertexCount,
                     true,
                     offX,
                     offY,
                     offZ,
                     false
                  );
                  dispatchCount++;
               }

               mesh.swapAnimatedBuffers();
            }
         }
      }

      GrassComputeAnimator.barrierAfterCompute();
      GrassComputeAnimator.end();
      if (!loggedFirstIrisComputeFrame && meshCount > 0) {
         loggedFirstIrisComputeFrame = true;
         LOGGER.info("[grassiergrass] Iris compute frame: meshes={}, computeMeshes={}, dispatches={}", new Object[]{meshCount, computeMeshCount, dispatchCount});
      }

      bakeStaticSections(meshes, cameraPos, noiseTextureId, trailTextureId);
   }

   private static void bakeStaticSections(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Vec3 cameraPos, int noiseTextureId, int trailTextureId) {
      int budget = 8;
      boolean began = false;
      ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(meshes);

      while (iterator.hasNext() && budget > 0) {
         Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
         GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
         if (mesh.anim == GrassSectionMesh.Anim.BAKE_PENDING && mesh.hasComputeBuffers()) {
            if (!began) {
               GrassComputeAnimator.begin(noiseTextureId, trailTextureId, true);
               began = true;
            }

            long key = entry.getLongKey();
            float offX = (float)(SectionPos.sectionToBlockCoord(SectionPos.x(key)) - cameraPos.x);
            float offY = (float)(SectionPos.sectionToBlockCoord(SectionPos.y(key)) - cameraPos.y);
            float offZ = (float)(SectionPos.sectionToBlockCoord(SectionPos.z(key)) - cameraPos.z);
            if (mesh.bladeInputGl != 0 && mesh.bladeBufferAlt != null) {
               GrassComputeAnimator.computeInto(
                  mesh.bladeInputGl, VboHandleAccess.vboId(mesh.bladeBufferAlt), mesh.bladeComputeLayout, mesh.bladeVertexCount, false, offX, offY, offZ, true
               );
            }

            if (mesh.plantInputGl != 0 && mesh.plantBufferAlt != null) {
               GrassComputeAnimator.computeInto(
                  mesh.plantInputGl, VboHandleAccess.vboId(mesh.plantBufferAlt), mesh.plantComputeLayout, mesh.plantVertexCount, true, offX, offY, offZ, true
               );
            }

            mesh.swapAnimatedBuffers();
            mesh.releaseComputeBuffers();
            mesh.anim = GrassSectionMesh.Anim.BAKED;
            budget--;
         }
      }

      if (began) {
         GrassComputeAnimator.barrierAfterCompute();
         GrassComputeAnimator.end();
      }
   }

   private static void drawPlantSectionsIris(
      Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f baseModelView, Frustum frustum, Vec3 cameraPos
   ) {
      ShaderInstance shader = GameRenderer.getRendertypeCutoutShader();
      if (shader != null) {
         drawCachedIris(GrassRenderType.irisTerrainPlants(), shader, meshes, projection, baseModelView, frustum, cameraPos, true);
      }
   }

   private static void drawCachedIris(
      RenderType renderType,
      ShaderInstance shader,
      Long2ObjectOpenHashMap<GrassSectionMesh> meshes,
      Matrix4f projection,
      Matrix4f baseModelView,
      Frustum frustum,
      Vec3 cameraPos,
      boolean plants
   ) {
      renderType.setupRenderState();
      shader.setDefaultUniforms(Mode.QUADS, baseModelView, projection, Minecraft.getInstance().getWindow());
      float brightness = plants ? GrassIrisBrightness.colorMultiplier(true) : 1.0F;
      shader.safeGetUniform("ColorModulator").set(brightness, brightness, brightness, 1.0F);
      shader.apply();
      boolean drewAnything = false;
      ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(meshes);

      while (iterator.hasNext()) {
         Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
         GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
         VertexBuffer buffer = plants ? mesh.plantBuffer : mesh.buffer;
         if (buffer != null) {
            long key = entry.getLongKey();
            int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
            int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
            int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
            if (frustum == null || frustum.isVisible(mesh.bounds)) {
               if (shader.CHUNK_OFFSET != null) {
                  shader.CHUNK_OFFSET.set((float)(originX - cameraPos.x), (float)(originY - cameraPos.y), (float)(originZ - cameraPos.z));
                  shader.CHUNK_OFFSET.upload();
               }

               buffer.bind();
               buffer.draw();
               drewAnything = true;
            }
         }
      }

      if (shader.CHUNK_OFFSET != null) {
         shader.CHUNK_OFFSET.set(0.0F, 0.0F, 0.0F);
         shader.CHUNK_OFFSET.upload();
      }

      shader.clear();
      if (drewAnything) {
         VertexBuffer.unbind();
      }

      renderType.clearRenderState();
   }

   static AABB sectionBounds(int originX, int originY, int originZ) {
      double horizontalPadding = 0.25;
      return new AABB(
         originX - horizontalPadding,
         originY,
         originZ - horizontalPadding,
         originX + 16.0 + horizontalPadding,
         originY + 22.0,
         originZ + 16.0 + horizontalPadding
      );
   }

   private static void configureNoiseTexture() {
      AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(NOISE_TEXTURE);
      int textureId = texture.getId();
      if (configuredNoiseTextureId != textureId) {
         texture.setFilter(true, false);
         texture.bind();
         RenderSystem.texParameter(3553, 10242, 10497);
         RenderSystem.texParameter(3553, 10243, 10497);
         configuredNoiseTextureId = textureId;
      }
   }

   private static void configureShapeTexture(ResourceLocation location) {
      AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
      int textureId = texture.getId();
      if (configuredShapeTextureId != textureId) {
         texture.setFilter(false, false);
         texture.bind();
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         configuredShapeTextureId = textureId;
      }
   }
}
