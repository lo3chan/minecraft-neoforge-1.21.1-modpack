package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.mixin.VertexBufferAccessor;
import com.mojang.blaze3d.vertex.VertexBuffer;

final class VboHandleAccess {
   private VboHandleAccess() {
   }

   static int vboId(VertexBuffer buffer) {
      return buffer == null ? 0 : ((VertexBufferAccessor)buffer).grassiergrass$getVertexBufferId();
   }
}
