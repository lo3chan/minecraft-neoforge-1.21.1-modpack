/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.caffeinemc.mods.sodium.api.math.MatrixHelper
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryUtil
 */
package me.flashyreese.mods.sodiumextra.client.render.vertex.formats;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class TextureColorVertex {
    public static final VertexFormat FORMAT = DefaultVertexFormat.POSITION_TEX_COLOR;
    public static final int STRIDE = 24;
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_TEXTURE = 12;
    private static final int OFFSET_COLOR = 20;

    public static void write(long ptr, Matrix4f matrix, float x, float y, float z, int color, float u, float v) {
        float xt = MatrixHelper.transformPositionX((Matrix4f)matrix, (float)x, (float)y, (float)z);
        float yt = MatrixHelper.transformPositionY((Matrix4f)matrix, (float)x, (float)y, (float)z);
        float zt = MatrixHelper.transformPositionZ((Matrix4f)matrix, (float)x, (float)y, (float)z);
        TextureColorVertex.write(ptr, xt, yt, zt, color, u, v);
    }

    public static void write(long ptr, float x, float y, float z, int color, float u, float v) {
        MemoryUtil.memPutFloat((long)(ptr + 0L), (float)x);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(ptr + 0L + 8L), (float)z);
        MemoryUtil.memPutFloat((long)(ptr + 12L), (float)u);
        MemoryUtil.memPutFloat((long)(ptr + 12L + 4L), (float)v);
        MemoryUtil.memPutInt((long)(ptr + 20L), (int)color);
    }
}

