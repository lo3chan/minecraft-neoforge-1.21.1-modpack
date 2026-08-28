/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.ColorAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.LightAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute
 *  net.caffeinemc.mods.sodium.api.vertex.attributes.common.TextureAttribute
 *  org.lwjgl.system.MemoryUtil
 */
package me.flashyreese.mods.sodiumextra.client.render.vertex.formats;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.ColorAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.LightAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.PositionAttribute;
import net.caffeinemc.mods.sodium.api.vertex.attributes.common.TextureAttribute;
import org.lwjgl.system.MemoryUtil;

public final class WeatherVertex {
    public static final VertexFormat FORMAT = DefaultVertexFormat.PARTICLE;
    public static final int STRIDE = 28;
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_TEXTURE = 12;
    private static final int OFFSET_COLOR = 20;
    private static final int OFFSET_LIGHT = 24;

    public static void put(long ptr, float x, float y, float z, float u, float v, int color, int light) {
        PositionAttribute.put((long)(ptr + 0L), (float)x, (float)y, (float)z);
        TextureAttribute.put((long)(ptr + 12L), (float)u, (float)v);
        ColorAttribute.set((long)(ptr + 20L), (int)color);
        LightAttribute.set((long)(ptr + 24L), (int)light);
    }

    public static void put(long ptr, float x, float y, float z, float u, float v, int color, int lightU, int lightV) {
        PositionAttribute.put((long)(ptr + 0L), (float)x, (float)y, (float)z);
        TextureAttribute.put((long)(ptr + 12L), (float)u, (float)v);
        ColorAttribute.set((long)(ptr + 20L), (int)color);
        MemoryUtil.memPutShort((long)(ptr + 24L), (short)((short)lightU));
        MemoryUtil.memPutShort((long)(ptr + 24L + 2L), (short)((short)lightV));
    }
}

