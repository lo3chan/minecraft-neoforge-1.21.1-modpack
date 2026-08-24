package com.seibel.distanthorizons.common.render.openGl.glObject;

import com.seibel.distanthorizons.common.render.openGl.glObject.enums.GLEnums;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftGLWrapper;
import org.lwjgl.opengl.GL33;

public class GLState implements AutoCloseable {
   private static final MinecraftGLWrapper GLMC = MinecraftGLWrapper.INSTANCE;
   public int program;
   public int vao;
   public int vbo;
   public int ebo;
   public int fbo;
   public int texture2D;
   public int activeTextureNumber;
   public int texture0;
   public int texture1;
   public int texture2;
   public int texture3;
   public int frameBufferTexture0;
   public int frameBufferTexture1;
   public int frameBufferDepthTexture;
   public boolean blend;
   public boolean scissor;
   public int blendEqRGB;
   public int blendEqAlpha;
   public int blendSrcColor;
   public int blendSrcAlpha;
   public int blendDstColor;
   public int blendDstAlpha;
   public boolean depth;
   public boolean writeToDepthBuffer;
   public int depthFunc;
   public boolean stencil;
   public int stencilFunc;
   public int stencilRef;
   public int stencilMask;
   public int[] view;
   public boolean cull;
   public int cullMode;
   public int polyMode;

   public GLState() {
      this.saveState();
   }

   public void saveState() {
      this.program = GL33.glGetInteger(35725);
      this.vao = GL33.glGetInteger(34229);
      this.vbo = GL33.glGetInteger(34964);
      this.ebo = GL33.glGetInteger(34965);
      this.fbo = GL33.glGetInteger(36006);
      this.texture2D = GL33.glGetInteger(32873);
      this.activeTextureNumber = GL33.glGetInteger(34016);
      GLMC.glActiveTexture(33984);
      this.texture0 = GL33.glGetInteger(32873);
      GLMC.glActiveTexture(33985);
      this.texture1 = GL33.glGetInteger(32873);
      GLMC.glActiveTexture(33986);
      this.texture2 = GL33.glGetInteger(32873);
      GLMC.glActiveTexture(33987);
      this.texture3 = GL33.glGetInteger(32873);
      GLMC.glActiveTexture(this.activeTextureNumber);
      if (this.fbo != 0) {
         this.frameBufferTexture0 = GL33.glGetFramebufferAttachmentParameteri(36160, 36064, 36049);
         this.frameBufferTexture1 = GL33.glGetFramebufferAttachmentParameteri(36160, 36065, 36049);
         int depthType = GL33.glGetFramebufferAttachmentParameteri(36160, 36096, 36048);
         this.frameBufferDepthTexture = depthType == 5890 ? GL33.glGetFramebufferAttachmentParameteri(36160, 36096, 36049) : 0;
      } else {
         this.frameBufferTexture0 = 0;
         this.frameBufferTexture1 = 0;
         this.frameBufferDepthTexture = 0;
      }

      this.blend = GL33.glIsEnabled(3042);
      this.scissor = GL33.glIsEnabled(3089);
      this.blendEqRGB = GL33.glGetInteger(32777);
      this.blendEqAlpha = GL33.glGetInteger(34877);
      this.blendSrcColor = GL33.glGetInteger(32969);
      this.blendSrcAlpha = GL33.glGetInteger(32971);
      this.blendDstColor = GL33.glGetInteger(32968);
      this.blendDstAlpha = GL33.glGetInteger(32970);
      this.depth = GL33.glIsEnabled(2929);
      this.writeToDepthBuffer = GL33.glGetInteger(2930) == 1;
      this.depthFunc = GL33.glGetInteger(2932);
      this.stencil = GL33.glIsEnabled(2960);
      this.stencilFunc = GL33.glGetInteger(2962);
      this.stencilRef = GL33.glGetInteger(2967);
      this.stencilMask = GL33.glGetInteger(2963);
      this.view = new int[4];
      GL33.glGetIntegerv(2978, this.view);
      this.cull = GL33.glIsEnabled(2884);
      this.cullMode = GL33.glGetInteger(2885);
      this.polyMode = GL33.glGetInteger(2880);
   }

   @Override
   public void close() {
      GLMC.glBindFramebuffer(36160, 0);
      boolean frameBufferSet = false;
      if (this.fbo != 0 && GL33.glIsFramebuffer(this.fbo)) {
         GLMC.glBindFramebuffer(36160, this.fbo);
         frameBufferSet = true;
      }

      if (this.blend) {
         GLMC.enableBlend();
      } else {
         GLMC.disableBlend();
      }

      if (this.scissor) {
         GLMC.enableScissorTest();
      } else {
         GLMC.disableScissorTest();
      }

      GLMC.glActiveTexture(33984);
      GLMC.glBindTexture(GL33.glIsTexture(this.texture0) ? this.texture0 : 0);
      GLMC.glActiveTexture(33985);
      GLMC.glBindTexture(GL33.glIsTexture(this.texture1) ? this.texture1 : 0);
      GLMC.glActiveTexture(33986);
      GLMC.glBindTexture(GL33.glIsTexture(this.texture2) ? this.texture2 : 0);
      GLMC.glActiveTexture(33987);
      GLMC.glBindTexture(GL33.glIsTexture(this.texture3) ? this.texture3 : 0);
      GLMC.glActiveTexture(this.activeTextureNumber);
      GLMC.glBindTexture(GL33.glIsTexture(this.texture2D) ? this.texture2D : 0);
      if (frameBufferSet) {
         if (GL33.glIsTexture(this.frameBufferTexture0)) {
            GL33.glFramebufferTexture2D(36160, 36064, 3553, this.frameBufferTexture0, 0);
         }

         if (this.frameBufferTexture1 != 0 && GL33.glIsTexture(this.frameBufferTexture1)) {
            GL33.glFramebufferTexture2D(36160, 36065, 3553, this.frameBufferTexture1, 0);
         }

         if (GL33.glIsTexture(this.frameBufferDepthTexture)) {
            GL33.glFramebufferTexture2D(36160, 36096, 3553, this.frameBufferDepthTexture, 0);
         }
      }

      GL33.glBindVertexArray(GL33.glIsVertexArray(this.vao) ? this.vao : 0);
      GL33.glBindBuffer(34962, GL33.glIsBuffer(this.vbo) ? this.vbo : 0);
      GL33.glBindBuffer(34963, GL33.glIsBuffer(this.ebo) ? this.ebo : 0);
      GL33.glUseProgram(GL33.glIsProgram(this.program) ? this.program : 0);
      if (this.writeToDepthBuffer) {
         GLMC.enableDepthMask();
      } else {
         GLMC.disableDepthMask();
      }

      GLMC.glBlendFunc(this.blendSrcColor, this.blendDstColor);
      GL33.glBlendEquationSeparate(this.blendEqRGB, this.blendEqAlpha);
      GLMC.glBlendFuncSeparate(this.blendSrcColor, this.blendDstColor, this.blendSrcAlpha, this.blendDstAlpha);
      if (this.depth) {
         GLMC.enableDepthTest();
      } else {
         GLMC.disableDepthTest();
      }

      GLMC.glDepthFunc(this.depthFunc);
      if (this.stencil) {
         GL33.glEnable(2960);
      } else {
         GL33.glDisable(2960);
      }

      GL33.glStencilFunc(this.stencilFunc, this.stencilRef, this.stencilMask);
      GL33.glViewport(this.view[0], this.view[1], this.view[2], this.view[3]);
      if (this.cull) {
         GLMC.enableFaceCulling();
      } else {
         GLMC.disableFaceCulling();
      }

      GL33.glCullFace(this.cullMode);
      GL33.glPolygonMode(1032, this.polyMode);
   }

   @Override
   public String toString() {
      return "GLState{program="
         + this.program
         + ", vao="
         + this.vao
         + ", vbo="
         + this.vbo
         + ", ebo="
         + this.ebo
         + ", fbo="
         + this.fbo
         + ", text="
         + GLEnums.getString(this.texture2D)
         + "@"
         + this.activeTextureNumber
         + ", text0="
         + GLEnums.getString(this.texture0)
         + ", FB text0="
         + this.frameBufferTexture0
         + ", FB text1="
         + this.frameBufferTexture1
         + ", FB depth="
         + this.frameBufferDepthTexture
         + ", blend="
         + this.blend
         + ", scissor="
         + this.scissor
         + ", blendMode="
         + GLEnums.getString(this.blendSrcColor)
         + ","
         + GLEnums.getString(this.blendDstColor)
         + ", depth="
         + this.depth
         + ", depthFunc="
         + GLEnums.getString(this.depthFunc)
         + ", stencil="
         + this.stencil
         + ", stencilFunc="
         + GLEnums.getString(this.stencilFunc)
         + ", stencilRef="
         + this.stencilRef
         + ", stencilMask="
         + this.stencilMask
         + ", view={x:"
         + this.view[0]
         + ", y:"
         + this.view[1]
         + ", w:"
         + this.view[2]
         + ", h:"
         + this.view[3]
         + "}, cull="
         + this.cull
         + ", cullMode="
         + GLEnums.getString(this.cullMode)
         + ", polyMode="
         + GLEnums.getString(this.polyMode)
         + "}";
   }
}
