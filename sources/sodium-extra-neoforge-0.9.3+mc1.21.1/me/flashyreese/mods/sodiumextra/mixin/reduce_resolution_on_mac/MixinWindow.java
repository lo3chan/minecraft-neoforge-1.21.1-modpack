package me.flashyreese.mods.sodiumextra.mixin.reduce_resolution_on_mac;

import com.mojang.blaze3d.platform.Window;
import me.flashyreese.mods.sodiumextra.client.util.MacReducedResolution;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Window.class})
public class MixinWindow {
   @Shadow
   private int width;
   @Shadow
   private int height;
   @Shadow
   private int framebufferWidth;
   @Shadow
   private int framebufferHeight;

   @Redirect(
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V"
      ),
      method = {"<init>"},
      remap = false
   )
   private void onDefaultWindowHints() {
      GLFW.glfwDefaultWindowHints();
      MacReducedResolution.useOpenGlBackend();
      if (MacReducedResolution.isEnabled()) {
         GLFW.glfwWindowHint(143361, 0);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"refreshFramebufferSize"}
   )
   private void afterUpdateFrameBufferSize(CallbackInfo ci) {
      this.scaleInitialFramebufferSize();
   }

   @Inject(
      method = {"onFramebufferResize"},
      at = {@At(
         value = "FIELD",
         target = "Lcom/mojang/blaze3d/platform/Window;framebufferHeight:I",
         opcode = 181,
         shift = Shift.AFTER
      )}
   )
   private void afterFramebufferResize(long handle, int newWidth, int newHeight, CallbackInfo ci) {
      this.scaleFramebufferSize();
   }

   @Unique
   private void scaleInitialFramebufferSize() {
      if (MacReducedResolution.shouldUseWindowSizeForInitialFramebuffer()) {
         this.framebufferWidth = Math.max(1, this.width);
         this.framebufferHeight = Math.max(1, this.height);
      } else {
         this.scaleFramebufferSize();
      }
   }

   @Unique
   private void scaleFramebufferSize() {
      if (MacReducedResolution.shouldReduceFramebuffer()) {
         this.framebufferWidth = MacReducedResolution.reduce(this.framebufferWidth);
         this.framebufferHeight = MacReducedResolution.reduce(this.framebufferHeight);
      }
   }
}
