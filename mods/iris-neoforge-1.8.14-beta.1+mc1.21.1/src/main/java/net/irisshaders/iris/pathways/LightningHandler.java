/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.resources.ResourceLocation
 */
package net.irisshaders.iris.pathways;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.irisshaders.iris.layer.InnerWrappedRenderType;
import net.irisshaders.iris.layer.LightningRenderStateShard;
import net.irisshaders.iris.pipeline.programs.ShaderAccess;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class LightningHandler
extends RenderType {
    public static final RenderType IRIS_LIGHTNING = new InnerWrappedRenderType("iris_lightning2", (RenderType)RenderType.create((String)"iris_lightning", (VertexFormat)DefaultVertexFormat.POSITION_COLOR, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(false)), new LightningRenderStateShard());
    public static final Function<ResourceLocation, RenderType> MEKANISM_FLAME = Util.memoize(resourceLocation -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(ShaderAccess::getMekanismFlameShader)).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true);
        return LightningHandler.create((String)"mek_flame", (VertexFormat)DefaultVertexFormat.POSITION_TEX_COLOR, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)state);
    });
    public static final RenderType MEKASUIT = LightningHandler.create((String)"mekasuit", (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)131072, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(ShaderAccess::getMekasuitShader)).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    public static final Function<ResourceLocation, RenderType> SPS = Util.memoize(r -> LightningHandler.create((String)"sps", (VertexFormat)DefaultVertexFormat.POSITION_TEX_COLOR, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(ShaderAccess::getSPSShader)).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(r, false, false)).setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY).createCompositeState(true)));

    public LightningHandler(String pRenderType0, VertexFormat pVertexFormat1, VertexFormat.Mode pVertexFormat$Mode2, int pInt3, boolean pBoolean4, boolean pBoolean5, Runnable pRunnable6, Runnable pRunnable7) {
        super(pRenderType0, pVertexFormat1, pVertexFormat$Mode2, pInt3, pBoolean4, pBoolean5, pRunnable6, pRunnable7);
    }
}

