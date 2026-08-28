/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.slf4j.Logger
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassComputeAnimator;
import com.leonardoinc22.shortgrass.client.render.GrassRenderType;
import com.leonardoinc22.shortgrass.client.render.GrassSectionMesh;
import com.leonardoinc22.shortgrass.client.render.GrassShaderUniforms;
import com.leonardoinc22.shortgrass.client.render.GrassTrailField;
import com.leonardoinc22.shortgrass.client.render.VboHandleAccess;
import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
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
    private static final ResourceLocation NOISE_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"textures/effect/noise.png");
    private static final ResourceLocation BANDS_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"textures/block/grass_bands.png");
    private static final ResourceLocation TAPERED_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"textures/block/grass_tapered.png");
    private static final Matrix4f MODEL_VIEW_SCRATCH = new Matrix4f();
    private static int configuredNoiseTextureId = -1;
    private static int configuredShapeTextureId = -1;
    private static boolean loggedFirstIrisComputeFrame;
    private static final int STATIC_BAKE_BUDGET_PER_FRAME = 8;

    private GrassDrawDispatcher() {
    }

    static void drawSections(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f modelView, Frustum frustum, ClientLevel level, Vec3 cameraPos, float partialTick) {
        GrassTrailField.update(level, cameraPos, partialTick);
        float windTime = GrassShaderUniforms.advanceRenderAnimationTicks();
        ShaderInstance shader = GrassRenderType.getGrassShader();
        GrassShaderUniforms.updateAnimationUniforms(shader, windTime, cameraPos);
        shader.safeGetUniform("GrassBrightness").set(1.0f);
        GrassRenderType.grassBlades().setupRenderState();
        GrassDrawDispatcher.configureNoiseTexture();
        boolean segmented = GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED;
        ResourceLocation shapeTexture = segmented ? BANDS_TEXTURE : TAPERED_TEXTURE;
        GrassDrawDispatcher.configureShapeTexture(shapeTexture);
        RenderSystem.setShaderTexture((int)3, (ResourceLocation)NOISE_TEXTURE);
        RenderSystem.setShaderTexture((int)4, (ResourceLocation)GrassTrailField.textureLocation());
        RenderSystem.setShaderTexture((int)5, (ResourceLocation)shapeTexture);
        boolean drewAnything = false;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(meshes);
        while (iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            VertexBuffer buffer = mesh.buffer;
            if (buffer == null) continue;
            long key = entry.getLongKey();
            int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
            int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
            int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
            if (frustum != null && !frustum.isVisible(mesh.bounds)) continue;
            shader.safeGetUniform("SectionOffset").set((float)((double)originX - cameraPos.x), (float)((double)originY - cameraPos.y), (float)((double)originZ - cameraPos.z));
            buffer.bind();
            buffer.drawWithShader(modelView, projection, shader);
            drewAnything = true;
        }
        if (drewAnything) {
            VertexBuffer.unbind();
        }
        GrassRenderType.grassBlades().clearRenderState();
        GrassDrawDispatcher.drawPlantSections(meshes, projection, modelView, frustum, cameraPos, windTime);
    }

    private static void drawPlantSections(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f modelView, Frustum frustum, Vec3 cameraPos, float windTime) {
        ShaderInstance plantShader = GrassRenderType.getPlantShader();
        if (plantShader == null) {
            return;
        }
        GrassShaderUniforms.setSharedWindUniforms(plantShader, windTime, cameraPos);
        GrassRenderType.grassPlants().setupRenderState();
        GrassDrawDispatcher.configureNoiseTexture();
        RenderSystem.setShaderTexture((int)3, (ResourceLocation)NOISE_TEXTURE);
        RenderSystem.setShaderTexture((int)4, (ResourceLocation)GrassTrailField.textureLocation());
        boolean drewAnything = false;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(meshes);
        while (iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            VertexBuffer buffer = mesh.plantBuffer;
            if (buffer == null) continue;
            long key = entry.getLongKey();
            int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
            int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
            int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
            if (frustum != null && !frustum.isVisible(mesh.bounds)) continue;
            plantShader.safeGetUniform("SectionOffset").set((float)((double)originX - cameraPos.x), (float)((double)originY - cameraPos.y), (float)((double)originZ - cameraPos.z));
            buffer.bind();
            buffer.drawWithShader(modelView, projection, plantShader);
            drewAnything = true;
        }
        if (drewAnything) {
            VertexBuffer.unbind();
        }
        GrassRenderType.grassPlants().clearRenderState();
    }

    static void drawSectionsIris(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f baseModelView, Frustum frustum, ClientLevel level, Vec3 cameraPos, float partialTick, boolean computeMode) {
        ShaderInstance shader = GameRenderer.getRendertypeCutoutShader();
        if (shader == null) {
            return;
        }
        GrassTrailField.update(level, cameraPos, partialTick);
        float windTime = GrassShaderUniforms.advanceRenderAnimationTicks();
        GrassShaderUniforms.updateFrameState(windTime, cameraPos);
        if (computeMode) {
            GrassDrawDispatcher.animateSectionsCompute(meshes, frustum, cameraPos);
        }
        GrassDrawDispatcher.drawCachedIris(GrassRenderType.irisTerrainBlades(), shader, meshes, projection, baseModelView, frustum, cameraPos, false);
        GrassDrawDispatcher.drawPlantSectionsIris(meshes, projection, baseModelView, frustum, cameraPos);
    }

    private static void animateSectionsCompute(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Frustum frustum, Vec3 cameraPos) {
        GrassDrawDispatcher.configureNoiseTexture();
        int noiseTextureId = Minecraft.getInstance().getTextureManager().getTexture(NOISE_TEXTURE).getId();
        int trailTextureId = GrassTrailField.textureId();
        GrassComputeAnimator.begin(noiseTextureId, trailTextureId, false);
        int meshCount = 0;
        int computeMeshCount = 0;
        int dispatchCount = 0;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(meshes);
        while (iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            ++meshCount;
            if (mesh.anim != GrassSectionMesh.Anim.ANIMATED || !mesh.hasComputeBuffers()) continue;
            ++computeMeshCount;
            long key = entry.getLongKey();
            if (frustum != null && !frustum.isVisible(mesh.bounds)) continue;
            float offX = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key)) - cameraPos.x);
            float offY = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key)) - cameraPos.y);
            float offZ = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key)) - cameraPos.z);
            if (mesh.bladeInputGl != 0 && mesh.bladeBufferAlt != null) {
                GrassComputeAnimator.computeInto(mesh.bladeInputGl, VboHandleAccess.vboId(mesh.bladeBufferAlt), mesh.bladeComputeLayout, mesh.bladeVertexCount, false, offX, offY, offZ, false);
                ++dispatchCount;
            }
            if (mesh.plantInputGl != 0 && mesh.plantBufferAlt != null) {
                GrassComputeAnimator.computeInto(mesh.plantInputGl, VboHandleAccess.vboId(mesh.plantBufferAlt), mesh.plantComputeLayout, mesh.plantVertexCount, true, offX, offY, offZ, false);
                ++dispatchCount;
            }
            mesh.swapAnimatedBuffers();
        }
        GrassComputeAnimator.barrierAfterCompute();
        GrassComputeAnimator.end();
        if (!loggedFirstIrisComputeFrame && meshCount > 0) {
            loggedFirstIrisComputeFrame = true;
            LOGGER.info("[grassiergrass] Iris compute frame: meshes={}, computeMeshes={}, dispatches={}", new Object[]{meshCount, computeMeshCount, dispatchCount});
        }
        GrassDrawDispatcher.bakeStaticSections(meshes, cameraPos, noiseTextureId, trailTextureId);
    }

    private static void bakeStaticSections(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Vec3 cameraPos, int noiseTextureId, int trailTextureId) {
        int budget = 8;
        boolean began = false;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(meshes);
        while (iterator.hasNext() && budget > 0) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            if (mesh.anim != GrassSectionMesh.Anim.BAKE_PENDING || !mesh.hasComputeBuffers()) continue;
            if (!began) {
                GrassComputeAnimator.begin(noiseTextureId, trailTextureId, true);
                began = true;
            }
            long key = entry.getLongKey();
            float offX = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key)) - cameraPos.x);
            float offY = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key)) - cameraPos.y);
            float offZ = (float)((double)SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key)) - cameraPos.z);
            if (mesh.bladeInputGl != 0 && mesh.bladeBufferAlt != null) {
                GrassComputeAnimator.computeInto(mesh.bladeInputGl, VboHandleAccess.vboId(mesh.bladeBufferAlt), mesh.bladeComputeLayout, mesh.bladeVertexCount, false, offX, offY, offZ, true);
            }
            if (mesh.plantInputGl != 0 && mesh.plantBufferAlt != null) {
                GrassComputeAnimator.computeInto(mesh.plantInputGl, VboHandleAccess.vboId(mesh.plantBufferAlt), mesh.plantComputeLayout, mesh.plantVertexCount, true, offX, offY, offZ, true);
            }
            mesh.swapAnimatedBuffers();
            mesh.releaseComputeBuffers();
            mesh.anim = GrassSectionMesh.Anim.BAKED;
            --budget;
        }
        if (began) {
            GrassComputeAnimator.barrierAfterCompute();
            GrassComputeAnimator.end();
        }
    }

    private static void drawPlantSectionsIris(Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f baseModelView, Frustum frustum, Vec3 cameraPos) {
        ShaderInstance shader = GameRenderer.getRendertypeCutoutShader();
        if (shader == null) {
            return;
        }
        GrassDrawDispatcher.drawCachedIris(GrassRenderType.irisTerrainPlants(), shader, meshes, projection, baseModelView, frustum, cameraPos, true);
    }

    private static void drawCachedIris(RenderType renderType, ShaderInstance shader, Long2ObjectOpenHashMap<GrassSectionMesh> meshes, Matrix4f projection, Matrix4f baseModelView, Frustum frustum, Vec3 cameraPos, boolean plants) {
        renderType.setupRenderState();
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, baseModelView, projection, Minecraft.getInstance().getWindow());
        float brightness = plants ? GrassIrisBrightness.colorMultiplier(true) : 1.0f;
        shader.safeGetUniform("ColorModulator").set(brightness, brightness, brightness, 1.0f);
        shader.apply();
        boolean drewAnything = false;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(meshes);
        while (iterator.hasNext()) {
            VertexBuffer buffer;
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            GrassSectionMesh mesh = (GrassSectionMesh)entry.getValue();
            VertexBuffer vertexBuffer = buffer = plants ? mesh.plantBuffer : mesh.buffer;
            if (buffer == null) continue;
            long key = entry.getLongKey();
            int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
            int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
            int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
            if (frustum != null && !frustum.isVisible(mesh.bounds)) continue;
            if (shader.CHUNK_OFFSET != null) {
                shader.CHUNK_OFFSET.set((float)((double)originX - cameraPos.x), (float)((double)originY - cameraPos.y), (float)((double)originZ - cameraPos.z));
                shader.CHUNK_OFFSET.upload();
            }
            buffer.bind();
            buffer.draw();
            drewAnything = true;
        }
        if (shader.CHUNK_OFFSET != null) {
            shader.CHUNK_OFFSET.set(0.0f, 0.0f, 0.0f);
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
        return new AABB((double)originX - horizontalPadding, (double)originY, (double)originZ - horizontalPadding, (double)originX + 16.0 + horizontalPadding, (double)originY + 22.0, (double)originZ + 16.0 + horizontalPadding);
    }

    private static void configureNoiseTexture() {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(NOISE_TEXTURE);
        int textureId = texture.getId();
        if (configuredNoiseTextureId == textureId) {
            return;
        }
        texture.setFilter(true, false);
        texture.bind();
        RenderSystem.texParameter((int)3553, (int)10242, (int)10497);
        RenderSystem.texParameter((int)3553, (int)10243, (int)10497);
        configuredNoiseTextureId = textureId;
    }

    private static void configureShapeTexture(ResourceLocation location) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
        int textureId = texture.getId();
        if (configuredShapeTextureId == textureId) {
            return;
        }
        texture.setFilter(false, false);
        texture.bind();
        RenderSystem.texParameter((int)3553, (int)10242, (int)33071);
        RenderSystem.texParameter((int)3553, (int)10243, (int)33071);
        configuredShapeTextureId = textureId;
    }
}

