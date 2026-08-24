package com.seibel.distanthorizons.common.render.openGl.postProcessing;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlAbstractVertexAttribute;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexPointer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.GL33;

public class GlScreenQuad {
   public static GlScreenQuad INSTANCE = new GlScreenQuad();
   private static final float[] BOX_VERTICES = new float[]{-1.0F, -1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F};
   private GLVertexBuffer boxBuffer;
   private GlAbstractVertexAttribute va;
   private boolean init = false;

   private GlScreenQuad() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         this.va = GlAbstractVertexAttribute.create();
         this.va.bind();
         this.va.setVertexAttribute(0, 0, GlVertexPointer.addVec2Pointer(false));
         this.va.completeAndCheck(8);
         this.createBuffer();
      }
   }

   private void createBuffer() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(BOX_VERTICES.length * 4);
      buffer.order(ByteOrder.nativeOrder());
      buffer.asFloatBuffer().put(BOX_VERTICES);
      buffer.rewind();
      this.boxBuffer = new GLVertexBuffer(false);
      this.boxBuffer.bind();
      this.boxBuffer.uploadBuffer(buffer, BOX_VERTICES.length, EDhApiGpuUploadMethod.DATA, BOX_VERTICES.length * 4);
   }

   public void render() {
      this.init();
      this.boxBuffer.bind();
      this.va.bind();
      this.va.bindBufferToAllBindingPoints(this.boxBuffer.getId());
      GL33.glPolygonMode(1032, 6914);
      GL33.glDrawArrays(4, 0, 6);
      this.boxBuffer.unbind();
   }
}
