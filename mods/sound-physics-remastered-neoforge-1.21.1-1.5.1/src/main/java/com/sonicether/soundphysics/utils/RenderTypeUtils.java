/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$LineStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeRenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 */
package com.sonicether.soundphysics.utils;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class RenderTypeUtils {
    public static final RenderType.CompositeRenderType DEBUG_LINE_STRIP_SEETHROUGH;
    public static final RenderType DEBUG_LINE_STRIP;

    static {
        DEBUG_LINE_STRIP = RenderType.debugLineStrip((double)1.0);
        DEBUG_LINE_STRIP_SEETHROUGH = RenderType.create((String)"debug_line_strip_seethrough", (VertexFormat)DefaultVertexFormat.POSITION_COLOR, (VertexFormat.Mode)VertexFormat.Mode.DEBUG_LINE_STRIP, (int)1536, (boolean)false, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(1.0))).setTransparencyState(RenderStateShard.NO_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL).setDepthTestState(RenderStateShard.NO_DEPTH_TEST).createCompositeState(false));
    }
}

