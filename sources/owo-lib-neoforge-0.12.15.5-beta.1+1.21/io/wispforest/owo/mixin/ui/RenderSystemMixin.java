package io.wispforest.owo.mixin.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.util.ScissorStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {RenderSystem.class},
   remap = false
)
public class RenderSystemMixin {
   @Inject(
      method = {"enableScissor(IIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void pushScissors(int x, int y, int width, int height, CallbackInfo ci) {
      if (OwoUIAdapter.isRendering()) {
         ScissorStack.pushDirect(x, y, width, height);
         ci.cancel();
      }
   }

   @Inject(
      method = {"disableScissor()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void popScissors(CallbackInfo ci) {
      if (OwoUIAdapter.isRendering()) {
         ScissorStack.pop();
         ci.cancel();
      }
   }
}
