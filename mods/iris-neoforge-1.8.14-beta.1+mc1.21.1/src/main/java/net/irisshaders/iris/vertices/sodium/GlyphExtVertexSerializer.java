/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 *  org.joml.Vector3f
 */
package net.irisshaders.iris.vertices.sodium;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.MemoryAccess;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import net.irisshaders.iris.vertices.sodium.QuadViewEntity;
import org.joml.Vector3f;

public class GlyphExtVertexSerializer
implements VertexSerializer {
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_COLOR = 12;
    private static final int OFFSET_TEXTURE = 16;
    private static final int OFFSET_MID_TEXTURE = IrisVertexFormats.GLYPH.getOffset(IrisVertexFormats.MID_TEXTURE_ELEMENT);
    private static final int OFFSET_LIGHT = 24;
    private static final int OFFSET_NORMAL = IrisVertexFormats.GLYPH.getOffset(VertexFormatElement.NORMAL);
    private static final int OFFSET_TANGENT = IrisVertexFormats.GLYPH.getOffset(IrisVertexFormats.TANGENT_ELEMENT);
    private static final QuadViewEntity quad = new QuadViewEntity();
    private static final Vector3f saveNormal = new Vector3f();
    private static final int STRIDE = IrisVertexFormats.GLYPH.getVertexSize();

    private static void endQuad(float uSum, float vSum, long src, long dst) {
        uSum *= 0.25f;
        vSum *= 0.25f;
        quad.setup(dst, IrisVertexFormats.GLYPH.getVertexSize());
        NormalHelper.computeFaceNormal(saveNormal, quad);
        float normalX = GlyphExtVertexSerializer.saveNormal.x;
        float normalY = GlyphExtVertexSerializer.saveNormal.y;
        float normalZ = GlyphExtVertexSerializer.saveNormal.z;
        int normal = NormI8.pack(saveNormal);
        int tangent = NormalHelper.computeTangent(normalX, normalY, normalZ, quad);
        for (long vertex = 0L; vertex < 4L; ++vertex) {
            MemoryAccess.setFloat(dst + (long)OFFSET_MID_TEXTURE - (long)STRIDE * vertex, uSum);
            MemoryAccess.setFloat(dst + (long)(OFFSET_MID_TEXTURE + 4) - (long)STRIDE * vertex, vSum);
            MemoryAccess.setInt(dst + (long)OFFSET_NORMAL - (long)STRIDE * vertex, normal);
            MemoryAccess.setInt(dst + (long)OFFSET_TANGENT - (long)STRIDE * vertex, tangent);
        }
    }

    public void serialize(long src, long dst, int vertexCount) {
        float uSum = 0.0f;
        float vSum = 0.0f;
        for (int i = 0; i < vertexCount; ++i) {
            float u = MemoryAccess.getFloat(src + 16L);
            float v = MemoryAccess.getFloat(src + 16L + 4L);
            uSum += u;
            vSum += v;
            MemoryIntrinsics.copyMemory((long)src, (long)dst, (int)28);
            MemoryAccess.setShort(dst + 32L, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
            MemoryAccess.setShort(dst + 34L, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
            MemoryAccess.setShort(dst + 36L, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem());
            if (i == 3) continue;
            src += (long)DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP.getVertexSize();
            dst += (long)IrisVertexFormats.GLYPH.getVertexSize();
        }
        GlyphExtVertexSerializer.endQuad(uSum, vSum, src, dst);
    }
}

