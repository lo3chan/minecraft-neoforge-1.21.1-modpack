/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  org.lwjgl.system.MemoryUtil
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassComputeAnimator;
import com.leonardoinc22.shortgrass.client.render.GrassSectionMesh;
import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.client.render.iris.IrisCompat;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.MemoryUtil;

final class GrassSectionBuildBuffers
implements AutoCloseable {
    private static final int PLANT_BUFFER_BYTES = 131072;
    private static final float VEGETATION_NORMAL_X = 0.0f;
    private static final float VEGETATION_NORMAL_Y = 1.0f;
    private static final float VEGETATION_NORMAL_Z = 0.0f;
    private static final float TAPERED_GRADIENT_BASE_SCALE = 0.75f;
    private final Encoding drawnEncoding;
    private final boolean irisBlockMarkers;
    private final TextureAtlasSprite bladeSprite;
    private final TextureAtlasSprite snowBladeSprite;
    private final boolean segmentedStyle;
    private float bladeBandScale = 1.0f;
    private float bladeBandColumnU = 0.5f;
    private final VertexBuffer.Usage usage;
    private final ByteBufferBuilder bladeBytes;
    private final ByteBufferBuilder plantBytes;
    private final ByteBufferBuilder bladeBytesAlt;
    private final ByteBufferBuilder plantBytesAlt;
    private final ByteBufferBuilder bladeBytesInput;
    private final ByteBufferBuilder plantBytesInput;
    private final BufferBuilder bladeDrawn;
    private final BufferBuilder bladeDrawnAlt;
    private final BufferBuilder bladeInput;
    private final BufferBuilder plantDrawn;
    private final BufferBuilder plantDrawnAlt;
    private final BufferBuilder plantInput;
    private final GrassSectionMesh.LightRunBuilder bladeLightRuns = new GrassSectionMesh.LightRunBuilder();
    private final GrassSectionMesh.LightRunBuilder plantLightRuns = new GrassSectionMesh.LightRunBuilder();
    private MeshData bladeData;
    private MeshData plantData;
    private MeshData bladeDataAlt;
    private MeshData plantDataAlt;
    private MeshData bladeInputData;
    private MeshData plantInputData;

    GrassSectionBuildBuffers(boolean irisMode, boolean computeMode, TextureAtlasSprite bladeSprite, TextureAtlasSprite snowBladeSprite) {
        this.drawnEncoding = irisMode ? Encoding.IRIS : Encoding.CUSTOM;
        this.irisBlockMarkers = irisMode;
        this.bladeSprite = bladeSprite;
        this.snowBladeSprite = snowBladeSprite;
        this.segmentedStyle = GrassConfig.grassStyle == GrassConfig.GrassStyle.SEGMENTED;
        this.usage = computeMode ? VertexBuffer.Usage.DYNAMIC : VertexBuffer.Usage.STATIC;
        int bladeBufferBytes = GrassSectionBuildBuffers.bladeBufferBytes();
        this.bladeBytes = new ByteBufferBuilder(bladeBufferBytes);
        this.plantBytes = new ByteBufferBuilder(131072);
        this.bladeBytesAlt = computeMode ? new ByteBufferBuilder(bladeBufferBytes) : null;
        this.plantBytesAlt = computeMode ? new ByteBufferBuilder(131072) : null;
        this.bladeBytesInput = computeMode ? new ByteBufferBuilder(bladeBufferBytes) : null;
        this.plantBytesInput = computeMode ? new ByteBufferBuilder(131072) : null;
        this.bladeDrawn = new BufferBuilder(this.bladeBytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        this.plantDrawn = new BufferBuilder(this.plantBytes, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        if (computeMode) {
            this.bladeDrawnAlt = new BufferBuilder(this.bladeBytesAlt, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            this.plantDrawnAlt = new BufferBuilder(this.plantBytesAlt, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            this.bladeInput = new BufferBuilder(this.bladeBytesInput, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            this.plantInput = new BufferBuilder(this.plantBytesInput, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        } else {
            this.bladeDrawnAlt = null;
            this.plantDrawnAlt = null;
            this.bladeInput = null;
            this.plantInput = null;
        }
    }

    float bladeColorBrightness() {
        return this.drawnEncoding == Encoding.IRIS ? GrassIrisBrightness.colorMultiplier(false) : GrassConfig.grassBrightness;
    }

    void buildCpu(GrassSectionMesh mesh) {
        VertexFormat format;
        mesh.lightRuns = this.bladeLightRuns.toArray();
        mesh.plantLightRuns = this.plantLightRuns.toArray();
        this.bladeData = this.bladeDrawn.build();
        if (this.bladeData != null) {
            mesh.vertexBytes = GrassSectionBuildBuffers.copyVertexBytes(this.bladeData);
            format = this.bladeData.drawState().format();
            mesh.vertexStride = format.getVertexSize();
            mesh.lightOffset = format.getOffset(VertexFormatElement.UV2);
            mesh.bladeVertexCount = this.bladeData.drawState().vertexCount();
        }
        this.plantData = this.plantDrawn.build();
        if (this.plantData != null) {
            mesh.plantVertexBytes = GrassSectionBuildBuffers.copyVertexBytes(this.plantData);
            format = this.plantData.drawState().format();
            mesh.plantVertexStride = format.getVertexSize();
            mesh.plantLightOffset = format.getOffset(VertexFormatElement.UV2);
            mesh.plantVertexCount = this.plantData.drawState().vertexCount();
        }
        if (this.bladeInput == null) {
            return;
        }
        this.bladeDataAlt = this.bladeDrawnAlt.build();
        this.plantDataAlt = this.plantDrawnAlt.build();
        this.bladeInputData = this.bladeInput.build();
        this.plantInputData = this.plantInput.build();
    }

    void uploadInto(GrassSectionMesh mesh) {
        VertexFormat bladeDrawnFormat = null;
        VertexFormat plantDrawnFormat = null;
        if (this.bladeData != null) {
            bladeDrawnFormat = this.bladeData.drawState().format();
            mesh.buffer = GrassSectionBuildBuffers.uploadBuffer(this.bladeData, this.usage);
        }
        if (this.plantData != null) {
            plantDrawnFormat = this.plantData.drawState().format();
            mesh.plantBuffer = GrassSectionBuildBuffers.uploadBuffer(this.plantData, this.usage);
        }
        if (this.bladeDataAlt != null) {
            mesh.bladeBufferAlt = GrassSectionBuildBuffers.uploadBuffer(this.bladeDataAlt, VertexBuffer.Usage.DYNAMIC);
        }
        if (this.plantDataAlt != null) {
            mesh.plantBufferAlt = GrassSectionBuildBuffers.uploadBuffer(this.plantDataAlt, VertexBuffer.Usage.DYNAMIC);
        }
        if (this.bladeInputData != null && bladeDrawnFormat != null) {
            mesh.bladeComputeLayout = GrassComputeAnimator.layout(this.bladeInputData.drawState().format(), bladeDrawnFormat);
            mesh.bladeInputGl = GrassComputeAnimator.createBufferFromBytes(this.bladeInputData.vertexBuffer());
        }
        if (this.plantInputData != null && plantDrawnFormat != null) {
            mesh.plantComputeLayout = GrassComputeAnimator.layout(this.plantInputData.drawState().format(), plantDrawnFormat);
            mesh.plantInputGl = GrassComputeAnimator.createBufferFromBytes(this.plantInputData.vertexBuffer());
        }
    }

    void beginBladeLightRun(int localX, int localY, int localZ) {
        this.bladeLightRuns.begin(localX, localY, localZ);
    }

    void finishBladeLightRun() {
        this.bladeLightRuns.finish();
    }

    void beginPlantLightRun(int localX, int localY, int localZ) {
        this.plantLightRuns.begin(localX, localY, localZ);
    }

    void finishPlantLightRun() {
        this.plantLightRuns.finish();
    }

    void setBladeBandColumn(float columnU) {
        this.bladeBandColumnU = columnU;
    }

    void beginBladeIrisBlock(BlockState state, int localX, int localY, int localZ, float bandScale) {
        this.bladeBandScale = bandScale;
        if (!this.irisBlockMarkers) {
            return;
        }
        IrisCompat.beginTerrainBlock((VertexConsumer)this.bladeDrawn, state, localX, localY, localZ);
        if (this.bladeDrawnAlt != null) {
            IrisCompat.beginTerrainBlock((VertexConsumer)this.bladeDrawnAlt, state, localX, localY, localZ);
        }
    }

    void endBladeIrisBlock() {
        if (!this.irisBlockMarkers) {
            return;
        }
        IrisCompat.endTerrainBlock((VertexConsumer)this.bladeDrawn);
        if (this.bladeDrawnAlt != null) {
            IrisCompat.endTerrainBlock((VertexConsumer)this.bladeDrawnAlt);
        }
    }

    void beginPlantIrisBlock(BlockState state, int localX, int localY, int localZ) {
        if (!this.irisBlockMarkers) {
            return;
        }
        IrisCompat.beginTerrainBlock((VertexConsumer)this.plantDrawn, state, localX, localY, localZ);
        if (this.plantDrawnAlt != null) {
            IrisCompat.beginTerrainBlock((VertexConsumer)this.plantDrawnAlt, state, localX, localY, localZ);
        }
    }

    void endPlantIrisBlock() {
        if (!this.irisBlockMarkers) {
            return;
        }
        IrisCompat.endTerrainBlock((VertexConsumer)this.plantDrawn);
        if (this.plantDrawnAlt != null) {
            IrisCompat.endTerrainBlock((VertexConsumer)this.plantDrawnAlt);
        }
    }

    void bladeVertex(float x, float y, float z, float u, float t, float sideSign, float angle, float noiseX, float noiseZ, int light, int tint, int heightClass, boolean snowSurface, float snowBlend) {
        int red = tint >> 16 & 0xFF;
        int green = tint >> 8 & 0xFF;
        int blue = tint & 0xFF;
        this.writeBlade((VertexConsumer)this.bladeDrawn, this.drawnEncoding, x, y, z, u, t, sideSign, angle, noiseX, noiseZ, light, red, green, blue, heightClass, snowSurface, snowBlend);
        if (this.bladeDrawnAlt != null) {
            this.writeBlade((VertexConsumer)this.bladeDrawnAlt, this.drawnEncoding, x, y, z, u, t, sideSign, angle, noiseX, noiseZ, light, red, green, blue, heightClass, snowSurface, snowBlend);
        }
        if (this.bladeInput != null) {
            this.writeBlade((VertexConsumer)this.bladeInput, Encoding.COMPUTE_INPUT, x, y, z, u, t, sideSign, angle, noiseX, noiseZ, light, red, green, blue, heightClass, snowSurface, snowBlend);
        }
        this.bladeLightRuns.addVertices(1);
    }

    void plantVertex(float x, float y, float z, float u, float v, float heightFraction, float normalX, float normalZ, int light, int red, int green, int blue) {
        GrassSectionBuildBuffers.writePlant((VertexConsumer)this.plantDrawn, this.drawnEncoding, x, y, z, u, v, heightFraction, normalX, normalZ, light, red, green, blue);
        if (this.plantDrawnAlt != null) {
            GrassSectionBuildBuffers.writePlant((VertexConsumer)this.plantDrawnAlt, this.drawnEncoding, x, y, z, u, v, heightFraction, normalX, normalZ, light, red, green, blue);
        }
        if (this.plantInput != null) {
            GrassSectionBuildBuffers.writePlant((VertexConsumer)this.plantInput, Encoding.COMPUTE_INPUT, x, y, z, u, v, heightFraction, normalX, normalZ, light, red, green, blue);
        }
        this.plantLightRuns.addVertices(1);
    }

    private void writeBlade(VertexConsumer consumer, Encoding encoding, float x, float y, float z, float u, float t, float sideSign, float angle, float noiseX, float noiseZ, int light, int red, int green, int blue, int heightClass, boolean snowSurface, float snowBlend) {
        switch (encoding.ordinal()) {
            case 0: {
                float spriteV;
                float spriteU;
                TextureAtlasSprite sprite;
                float gradient = Mth.lerp((float)snowBlend, (float)GrassSectionBuildBuffers.bladeGradient(t), (float)1.0f);
                TextureAtlasSprite textureAtlasSprite = sprite = snowSurface && this.segmentedStyle ? this.snowBladeSprite : this.bladeSprite;
                if (snowSurface && this.segmentedStyle) {
                    spriteU = 0.5f;
                    spriteV = 0.5f;
                } else if (this.segmentedStyle) {
                    spriteU = this.bladeBandColumnU;
                    spriteV = Math.min(t * this.bladeBandScale, 0.999f);
                } else {
                    spriteU = u;
                    spriteV = 1.0f - t;
                }
                consumer.addVertex(x, y, z).setColor(GrassSectionBuildBuffers.scaleColorComponent(red, gradient), GrassSectionBuildBuffers.scaleColorComponent(green, gradient), GrassSectionBuildBuffers.scaleColorComponent(blue, gradient), 255).setUv(sprite.getU(spriteU), sprite.getV(spriteV)).setLight(GrassIrisBrightness.adjustLight(light, false)).setNormal(0.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                consumer.addVertex(x, y, z).setColor(GrassSectionBuildBuffers.packBladeAlpha(sideSign, angle, heightClass), green, blue, Math.round(t * 255.0f)).setUv(noiseX, noiseZ).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                break;
            }
            case 1: {
                consumer.addVertex(x, y, z).setColor(red, green, blue, GrassSectionBuildBuffers.packBladeAlpha(sideSign, angle, heightClass)).setUv(u, snowBlend).setLight(light).setNormal(noiseX * 2.0f - 1.0f, noiseZ * 2.0f - 1.0f, t);
            }
        }
    }

    private static void writePlant(VertexConsumer consumer, Encoding encoding, float x, float y, float z, float u, float v, float heightFraction, float normalX, float normalZ, int light, int red, int green, int blue) {
        switch (encoding.ordinal()) {
            case 0: {
                consumer.addVertex(x, y, z).setColor(red, green, blue, 255).setUv(u, v).setLight(GrassIrisBrightness.adjustLight(light, true)).setNormal(0.0f, 1.0f, 0.0f);
                break;
            }
            case 2: {
                consumer.addVertex(x, y, z).setColor(red, green, blue, Math.round(heightFraction * 255.0f)).setUv(normalX * 0.5f + 0.5f, normalZ * 0.5f + 0.5f).setLight(light).setNormal(0.0f, 1.0f, 0.0f);
                break;
            }
            case 1: {
                consumer.addVertex(x, y, z).setColor(red, green, blue, 255).setUv(u, v).setLight(light).setNormal(normalX, normalZ, heightFraction);
            }
        }
    }

    private static int bladeBufferBytes() {
        long estimate = 256L * (long)Math.max(1, GrassConfig.bladesPerBlock) * 5L * 4L * 32L;
        return (int)Mth.clamp((long)estimate, (long)0x100000L, (long)0x1000000L);
    }

    private static VertexBuffer uploadBuffer(MeshData data, VertexBuffer.Usage usage) {
        VertexBuffer buffer = new VertexBuffer(usage);
        buffer.bind();
        buffer.upload(data);
        VertexBuffer.unbind();
        return buffer;
    }

    private static ByteBuffer copyVertexBytes(MeshData data) {
        ByteBuffer source = data.vertexBuffer().duplicate();
        source.position(0);
        ByteBuffer copy = MemoryUtil.memAlloc((int)source.remaining()).order(ByteOrder.nativeOrder());
        copy.put(source);
        copy.flip();
        return copy;
    }

    private static int scaleColorComponent(int value, float multiplier) {
        return Mth.clamp((int)Math.round((float)value * multiplier), (int)0, (int)255);
    }

    private static float bladeGradient(float t) {
        float shaped = Mth.clamp((float)t, (float)0.0f, (float)1.0f);
        float curve = Math.max(GrassConfig.bladeGradientCurve, 0.001f);
        if (curve != 1.0f) {
            shaped = (float)Math.pow(shaped, curve);
        }
        float bottom = GrassConfig.bladeGradientBottom;
        if (GrassConfig.grassStyle == GrassConfig.GrassStyle.TAPERED) {
            bottom *= 0.75f;
        }
        return Mth.lerp((float)shaped, (float)bottom, (float)GrassConfig.bladeGradientTop);
    }

    private static int packBladeAlpha(float sideSign, float angle, int heightClass) {
        int angleBucket = Mth.clamp((int)((int)(GrassSectionBuildBuffers.positiveAngle(angle) / ((float)Math.PI * 2) * 31.0f)), (int)0, (int)31);
        int packedHeightClass = Mth.clamp((int)heightClass, (int)0, (int)3) * 32;
        return (sideSign < 0.0f ? 0 : 128) + packedHeightClass + angleBucket;
    }

    private static float positiveAngle(float angle) {
        return angle - (float)Mth.floor((float)(angle / ((float)Math.PI * 2))) * ((float)Math.PI * 2);
    }

    @Override
    public void close() {
        this.bladeBytes.close();
        this.plantBytes.close();
        if (this.bladeBytesAlt != null) {
            this.bladeBytesAlt.close();
        }
        if (this.plantBytesAlt != null) {
            this.plantBytesAlt.close();
        }
        if (this.bladeBytesInput != null) {
            this.bladeBytesInput.close();
        }
        if (this.plantBytesInput != null) {
            this.plantBytesInput.close();
        }
    }

    private static enum Encoding {
        IRIS,
        CUSTOM,
        COMPUTE_INPUT;

    }
}

