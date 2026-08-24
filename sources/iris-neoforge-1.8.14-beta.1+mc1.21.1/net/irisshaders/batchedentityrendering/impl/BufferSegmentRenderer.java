package net.irisshaders.batchedentityrendering.impl;

import com.mojang.blaze3d.vertex.BufferUploader;

public class BufferSegmentRenderer {
   public void draw(BufferSegment segment) {
      if (segment.meshData() != null) {
         segment.type().draw(segment.meshData());
      }
   }

   public void drawInner(BufferSegment segment) {
      BufferUploader.drawWithShader(segment.meshData());
   }
}
