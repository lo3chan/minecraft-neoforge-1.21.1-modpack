package net.irisshaders.iris.vertices.sodium;

import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.MemoryAccess;
import net.irisshaders.iris.vertices.NormalHelper;

public class ModelToEntityVertexSerializer implements VertexSerializer {
   private static final int MIDCOORD = IrisVertexFormats.ENTITY.getOffset(IrisVertexFormats.MID_TEXTURE_ELEMENT);
   private static final int TANGENT = IrisVertexFormats.ENTITY.getOffset(IrisVertexFormats.TANGENT_ELEMENT);
   private static final int SRC_STRIDE = 36;
   private static final int DST_STRIDE = IrisVertexFormats.ENTITY.getVertexSize();

   public void serialize(long srcBase, long dstBase, int vertexCount) {
      int quadCount = vertexCount >> 2;
      short entity = (short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity();
      short blockEntity = (short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity();
      short item = (short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem();
      long src = srcBase;
      long dst = dstBase;

      for (int q = 0; q < quadCount; q++) {
         long v1 = src + 36L;
         long v2 = v1 + 36L;
         long v3 = v2 + 36L;
         int packedNormal = MemoryAccess.getInt(src + 32L);
         float nx = NormI8.unpackX(packedNormal);
         float ny = NormI8.unpackY(packedNormal);
         float nz = NormI8.unpackZ(packedNormal);
         float v0x = MemoryAccess.getFloat(src);
         float v0y = MemoryAccess.getFloat(src + 4L);
         float v0z = MemoryAccess.getFloat(src + 8L);
         float v0u = MemoryAccess.getFloat(src + 16L);
         float v0v = MemoryAccess.getFloat(src + 20L);
         float v1x = MemoryAccess.getFloat(v1);
         float v1y = MemoryAccess.getFloat(v1 + 4L);
         float v1z = MemoryAccess.getFloat(v1 + 8L);
         float v1u = MemoryAccess.getFloat(v1 + 16L);
         float v1v = MemoryAccess.getFloat(v1 + 20L);
         float v2x = MemoryAccess.getFloat(v2);
         float v2y = MemoryAccess.getFloat(v2 + 4L);
         float v2z = MemoryAccess.getFloat(v2 + 8L);
         float v2u = MemoryAccess.getFloat(v2 + 16L);
         float v2v = MemoryAccess.getFloat(v2 + 20L);
         int tangent = NormalHelper.computeTangent(null, nx, ny, nz, v0x, v0y, v0z, v0u, v0v, v1x, v1y, v1z, v1u, v1v, v2x, v2y, v2z, v2u, v2v);
         float midU = (v0u + v1u + v2u + MemoryAccess.getFloat(v3 + 16L)) * 0.25F;
         float midV = (v0v + v1v + v2v + MemoryAccess.getFloat(v3 + 20L)) * 0.25F;
         long writeSrc = src;
         long writeDst = dst;

         for (int i = 0; i < 4; i++) {
            MemoryIntrinsics.copyMemory(writeSrc, writeDst, 36);
            MemoryAccess.setShort(writeDst + 36L, entity);
            MemoryAccess.setShort(writeDst + 38L, blockEntity);
            MemoryAccess.setShort(writeDst + 40L, item);
            MemoryAccess.setFloat(writeDst + MIDCOORD, midU);
            MemoryAccess.setFloat(writeDst + MIDCOORD + 4L, midV);
            MemoryAccess.setInt(writeDst + TANGENT, tangent);
            writeSrc += 36L;
            writeDst += DST_STRIDE;
         }

         src += 144L;
         dst += DST_STRIDE * 4;
      }
   }
}
