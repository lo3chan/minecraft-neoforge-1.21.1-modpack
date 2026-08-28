/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer
 */
package net.irisshaders.iris.vertices.sodium;

import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.MemoryAccess;

public class IrisEntityToTerrainVertexSerializer
implements VertexSerializer {
    public void serialize(long src, long dst, int vertexCount) {
        for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex) {
            MemoryAccess.setFloat(dst, MemoryAccess.getFloat(src));
            MemoryAccess.setFloat(dst + 4L, MemoryAccess.getFloat(src + 4L));
            MemoryAccess.setFloat(dst + 8L, MemoryAccess.getFloat(src + 8L));
            MemoryAccess.setInt(dst + 12L, MemoryAccess.getInt(src + 12L));
            MemoryAccess.setFloat(dst + 16L, MemoryAccess.getFloat(src + 16L));
            MemoryAccess.setFloat(dst + 20L, MemoryAccess.getFloat(src + 20L));
            MemoryAccess.setInt(dst + 24L, MemoryAccess.getInt(src + 28L));
            MemoryAccess.setInt(dst + 28L, MemoryAccess.getInt(src + 32L));
            MemoryAccess.setInt(dst + 32L, 0);
            MemoryAccess.setInt(dst + 36L, MemoryAccess.getInt(src + 36L));
            MemoryAccess.setInt(dst + 40L, MemoryAccess.getInt(src + 40L));
            MemoryAccess.setInt(dst + 44L, MemoryAccess.getInt(src + 44L));
            src += (long)IrisVertexFormats.ENTITY.getVertexSize();
            dst += (long)IrisVertexFormats.TERRAIN.getVertexSize();
        }
    }
}

