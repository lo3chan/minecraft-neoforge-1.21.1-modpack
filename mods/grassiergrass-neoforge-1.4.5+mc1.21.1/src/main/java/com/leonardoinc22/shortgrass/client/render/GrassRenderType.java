/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.resources.ResourceLocation
 */
package com.leonardoinc22.shortgrass.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public final class GrassRenderType
extends RenderType {
    private static final ResourceLocation BLADE_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"grassiergrass", (String)"textures/block/blade.png");
    private static ShaderInstance grassShader;
    private static ShaderInstance plantShader;
    private static final RenderStateShard.ShaderStateShard GRASS_SHADER_SHARD;
    private static final RenderStateShard.ShaderStateShard PLANT_SHADER_SHARD;
    private static final RenderType GRASS_BLADES;
    private static final RenderType IRIS_TERRAIN_BLADES;
    private static final RenderType IRIS_TERRAIN_PLANTS;
    private static final RenderType GRASS_PLANTS;

    private GrassRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType grassBlades() {
        return GRASS_BLADES;
    }

    public static RenderType grassPlants() {
        return GRASS_PLANTS;
    }

    public static RenderType irisTerrainBlades() {
        return IRIS_TERRAIN_BLADES;
    }

    public static RenderType irisTerrainPlants() {
        return IRIS_TERRAIN_PLANTS;
    }

    public static void setGrassShader(ShaderInstance shader) {
        grassShader = shader;
    }

    public static ShaderInstance getGrassShader() {
        return grassShader;
    }

    public static void setPlantShader(ShaderInstance shader) {
        plantShader = shader;
    }

    public static ShaderInstance getPlantShader() {
        return plantShader;
    }

    static {
        GRASS_SHADER_SHARD = new RenderStateShard.ShaderStateShard(GrassRenderType::getGrassShader);
        PLANT_SHADER_SHARD = new RenderStateShard.ShaderStateShard(GrassRenderType::getPlantShader);
        GRASS_BLADES = GrassRenderType.create((String)"grassiergrass_blades", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)false, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(GRASS_SHADER_SHARD).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(BLADE_TEXTURE, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true));
        IRIS_TERRAIN_BLADES = GrassRenderType.create((String)"grassiergrass_iris_terrain_blades", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false)).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true));
        IRIS_TERRAIN_PLANTS = GrassRenderType.create((String)"grassiergrass_iris_terrain_plants", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false)).setLightmapState(LIGHTMAP).createCompositeState(true));
        GRASS_PLANTS = GrassRenderType.create((String)"grassiergrass_plants", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)false, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(PLANT_SHADER_SHARD).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(true));
    }
}

