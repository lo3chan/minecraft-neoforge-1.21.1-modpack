package com.seibel.distanthorizons.common.render.openGl;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLIndexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlAbstractVertexAttribute;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexPointer;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.util.math.DhMat4f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.GL33;

public class GlDhDebugWireframeRenderer extends AbstractDebugWireframeRenderer {
   public static GlDhDebugWireframeRenderer INSTANCE = new GlDhDebugWireframeRenderer();
   public static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   private GlShaderProgram basicShader;
   private GLVertexBuffer vertexBuffer;
   private GLIndexBuffer indexBuffer;
   private GlAbstractVertexAttribute va;
   private boolean init = false;
   private static final float[] BOX_VERTICES = new float[]{
      0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F
   };
   private static final int[] BOX_OUTLINE_INDICES = new int[]{0, 1, 1, 2, 2, 3, 3, 0, 4, 5, 5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7};

   private GlDhDebugWireframeRenderer() {
   }

   public void init() {
      if (!this.init) {
         this.init = true;
         this.va = GlAbstractVertexAttribute.create();
         this.va.bind();
         this.va.setVertexAttribute(0, 0, GlVertexPointer.addVec3Pointer(false));
         this.va.completeAndCheck(12);
         this.basicShader = new GlShaderProgram(
            "assets/distanthorizons/shaders/debug/gl/vert.vert", "assets/distanthorizons/shaders/debug/gl/frag.frag", "vPosition"
         );
         this.createBuffer();
      }
   }

   private void createBuffer() {
      ByteBuffer boxVerticesBuffer = ByteBuffer.allocateDirect(BOX_VERTICES.length * 4);
      boxVerticesBuffer.order(ByteOrder.nativeOrder());
      boxVerticesBuffer.asFloatBuffer().put(BOX_VERTICES);
      boxVerticesBuffer.rewind();
      this.vertexBuffer = new GLVertexBuffer(false);
      this.vertexBuffer.bind();
      this.vertexBuffer.uploadBuffer(boxVerticesBuffer, 8, EDhApiGpuUploadMethod.DATA, BOX_VERTICES.length * 4);
      ByteBuffer boxOutlineBuffer = ByteBuffer.allocateDirect(BOX_OUTLINE_INDICES.length * 4);
      boxOutlineBuffer.order(ByteOrder.nativeOrder());
      boxOutlineBuffer.asIntBuffer().put(BOX_OUTLINE_INDICES);
      boxOutlineBuffer.rewind();
      this.indexBuffer = new GLIndexBuffer(false);
      this.indexBuffer.uploadBuffer(boxOutlineBuffer, EDhApiGpuUploadMethod.DATA, BOX_OUTLINE_INDICES.length * 4, 35044);
   }

   @Override
   public void render(RenderParams renderParams) {
      this.init();
      GL33.glPolygonMode(1032, 6913);
      GLMC.enableDepthTest();
      this.basicShader.bind();
      this.va.bind();
      this.va.bindBufferToAllBindingPoints(this.vertexBuffer.getId());
      this.indexBuffer.bind();
      super.render(renderParams);
      GL33.glPolygonMode(1032, 6914);
   }

   @Override
   public void renderBox(AbstractDebugWireframeRenderer.Box box) {
      DhMat4f boxTransform = DhMat4f.createTranslateMatrix(
         box.minPos.x - this.camPosFloatThisFrame.x, box.minPos.y - this.camPosFloatThisFrame.y, box.minPos.z - this.camPosFloatThisFrame.z
      );
      boxTransform.multiply(DhMat4f.createScaleMatrix(box.maxPos.x - box.minPos.x, box.maxPos.y - box.minPos.y, box.maxPos.z - box.minPos.z));
      DhMat4f transformMatrix = this.dhMvmProjMatrixThisFrame.copy();
      transformMatrix.multiply(boxTransform);
      this.basicShader.setUniform(this.basicShader.getUniformLocation("uTransform"), transformMatrix);
      this.basicShader.setUniform(this.basicShader.getUniformLocation("uColor"), box.color);
      GL33.glDrawElements(1, BOX_OUTLINE_INDICES.length, 5125, 0L);
   }
}
