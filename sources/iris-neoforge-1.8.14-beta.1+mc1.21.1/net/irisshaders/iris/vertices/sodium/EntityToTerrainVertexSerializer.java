package net.irisshaders.iris.vertices.sodium;

import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.MemoryAccess;
import net.irisshaders.iris.vertices.NormalHelper;

public class EntityToTerrainVertexSerializer implements VertexSerializer {
   private static final int MIDCOORD = IrisVertexFormats.TERRAIN.getOffset(IrisVertexFormats.MID_TEXTURE_ELEMENT);
   private static final int TANGENT = IrisVertexFormats.TERRAIN.getOffset(IrisVertexFormats.TANGENT_ELEMENT);

   public void serialize(long src, long dst, int vertexCount) {
      int quadCount = vertexCount / 4;

      for (int i = 0; i < quadCount; i++) {
         int normal = MemoryAccess.getInt(src + 32L);
         int tangent = NormalHelper.computeTangent(
            null,
            NormI8.unpackX(normal),
            NormI8.unpackY(normal),
            NormI8.unpackZ(normal),
            MemoryAccess.getFloat(src),
            MemoryAccess.getFloat(src + 4L),
            MemoryAccess.getFloat(src + 8L),
            MemoryAccess.getFloat(src + 16L),
            MemoryAccess.getFloat(src + 20L),
            MemoryAccess.getFloat(src + 36L),
            MemoryAccess.getFloat(src + 4L + 36L),
            MemoryAccess.getFloat(src + 8L + 36L),
            MemoryAccess.getFloat(src + 16L + 36L),
            MemoryAccess.getFloat(src + 20L + 36L),
            MemoryAccess.getFloat(src + 36L + 36L),
            MemoryAccess.getFloat(src + 4L + 36L + 36L),
            MemoryAccess.getFloat(src + 8L + 36L + 36L),
            MemoryAccess.getFloat(src + 16L + 36L + 36L),
            MemoryAccess.getFloat(src + 20L + 36L + 36L)
         );
         float midU = 0.0F;
         float midV = 0.0F;

         for (int vertex = 0; vertex < 4; vertex++) {
            midU += MemoryAccess.getFloat(src + 16L + 36 * vertex);
            midV += MemoryAccess.getFloat(src + 20L + 36 * vertex);
         }

         midU /= 4.0F;
         midV /= 4.0F;

         for (int j = 0; j < 4; j++) {
            MemoryIntrinsics.copyMemory(src, dst, 24);
            MemoryAccess.setInt(dst + 24L, MemoryAccess.getInt(src + 28L));
            MemoryAccess.setInt(dst + 28L, normal);
            MemoryAccess.setShort(dst + 32L, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
            MemoryAccess.setShort(dst + 34L, (short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
            MemoryAccess.setFloat(dst + MIDCOORD, midU);
            MemoryAccess.setFloat(dst + MIDCOORD + 4L, midV);
            MemoryAccess.setInt(dst + TANGENT, tangent);
            MemoryAccess.setInt(dst + 48L, 0);
            src += 36L;
            dst += IrisVertexFormats.TERRAIN.getVertexSize();
         }
      }
   }
}
