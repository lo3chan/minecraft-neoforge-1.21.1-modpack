package com.seibel.distanthorizons.common.render.openGl.test;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.buffer.GLVertexBuffer;
import com.seibel.distanthorizons.common.render.openGl.glObject.shader.GlShaderProgram;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlAbstractVertexAttribute;
import com.seibel.distanthorizons.common.render.openGl.glObject.vertexAttribute.GlVertexPointer;
import com.seibel.distanthorizons.common.render.openGl.postProcessing.apply.GlDhApplyShader_neoforge;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhTestTriangleRenderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.GL33;

public class GlTestTriangleRenderer_neoforge implements IDhTestTriangleRenderer {
   public static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public static final GlTestTriangleRenderer_neoforge INSTANCE = new GlTestTriangleRenderer_neoforge();
   private static final float[] VERTICES = new float[]{
      -0.5F, -0.5F, 1.0F, 0.0F, 0.0F, 1.0F, 0.5F, -0.5F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F
   };
   GlShaderProgram basicShader;
   GLVertexBuffer vbo;
   GlAbstractVertexAttribute va;
   boolean init = false;

   private GlTestTriangleRenderer_neoforge() {
   }

   public void init() {
      if (!this.init) {
         LOGGER.info("init");
         this.init = true;
         this.va = GlAbstractVertexAttribute.create();
         this.va.bind();
         this.va.setVertexAttribute(0, 0, GlVertexPointer.addVec2Pointer(false));
         this.va.setVertexAttribute(0, 1, GlVertexPointer.addVec4Pointer(false));
         this.va.completeAndCheck(24);
         this.basicShader = new GlShaderProgram(
            "assets/distanthorizons/shaders/test/gl/vert.vert", "assets/distanthorizons/shaders/test/gl/frag.frag", new String[]{"vPosition", "color"}
         );
         this.createBuffer();
      }
   }

   private void createBuffer() {
      ByteBuffer buffer = ByteBuffer.allocateDirect(VERTICES.length * 4);
      buffer.order(ByteOrder.nativeOrder());
      buffer.asFloatBuffer().put(VERTICES);
      buffer.rewind();
      this.vbo = new GLVertexBuffer(false);
      this.vbo.bind();
      this.vbo.uploadBuffer(buffer, 3, EDhApiGpuUploadMethod.DATA, VERTICES.length * 4);
   }

   @Override
   public void render(RenderParams renderParams) {
      this.init();
      this.basicShader.bind();
      this.va.bind();
      this.vbo.bind();
      this.va.bindBufferToAllBindingPoints(this.vbo.getId());
      GL33.glDrawArrays(4, 0, 3);
      GlDhApplyShader_neoforge.INSTANCE.render(renderParams);
   }
}
