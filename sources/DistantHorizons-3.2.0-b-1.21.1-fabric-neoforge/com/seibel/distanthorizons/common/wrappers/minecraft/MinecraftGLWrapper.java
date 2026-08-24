package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import org.lwjgl.opengl.GL33;

public class MinecraftGLWrapper {
   public static final MinecraftGLWrapper INSTANCE = new MinecraftGLWrapper();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public void enableScissorTest() {
      GL33.glEnable(3089);
      GlStateManager._enableScissorTest();
   }

   public void disableScissorTest() {
      GL33.glDisable(3089);
      GlStateManager._disableScissorTest();
   }

   public void enableDepthTest() {
      GL33.glEnable(2929);
      GlStateManager._enableDepthTest();
   }

   public void disableDepthTest() {
      GL33.glDisable(2929);
      GlStateManager._disableDepthTest();
   }

   public void glDepthFunc(int func) {
      GL33.glDepthFunc(func);
      GlStateManager._depthFunc(func);
   }

   public int getActiveDepthFunc() {
      return GL33.glGetInteger(2932);
   }

   public void enableDepthMask() {
      GL33.glDepthMask(true);
      GlStateManager._depthMask(true);
   }

   public void disableDepthMask() {
      GL33.glDepthMask(false);
      GlStateManager._depthMask(false);
   }

   public void enableBlend() {
      GL33.glEnable(3042);
      GlStateManager._enableBlend();
   }

   public void disableBlend() {
      GL33.glDisable(3042);
      GlStateManager._disableBlend();
   }

   public void glBlendFunc(int sfactor, int dfactor) {
      GL33.glBlendFunc(sfactor, dfactor);
      GlStateManager._blendFunc(sfactor, dfactor);
   }

   public void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
      GL33.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
      GlStateManager._blendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
   }

   public void glBindFramebuffer(int target, int framebuffer) {
      GL33.glBindFramebuffer(target, framebuffer);
      GlStateManager._glBindFramebuffer(target, framebuffer);
   }

   public int glGenBuffers() {
      return GL33.glGenBuffers();
   }

   public void glDeleteBuffers(int buffer) {
      GL33.glDeleteBuffers(buffer);
   }

   public void enableFaceCulling() {
      GL33.glEnable(2884);
      GlStateManager._enableCull();
   }

   public void disableFaceCulling() {
      GL33.glDisable(2884);
      GlStateManager._disableCull();
   }

   public int glGenTextures() {
      return GlStateManager._genTexture();
   }

   public void glDeleteTextures(int texture) {
      GlStateManager._deleteTexture(texture);
   }

   public void glActiveTexture(int textureId) {
      GL33.glActiveTexture(textureId);
      GlStateManager._activeTexture(textureId);
   }

   public int getActiveTexture() {
      return GL33.glGetInteger(32873);
   }

   public void glBindTexture(int texture) {
      GL33.glBindTexture(3553, texture);
      GlStateManager._bindTexture(texture);
   }
}
