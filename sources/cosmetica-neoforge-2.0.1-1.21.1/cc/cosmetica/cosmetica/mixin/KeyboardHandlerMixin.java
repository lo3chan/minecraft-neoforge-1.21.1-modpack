package cc.cosmetica.cosmetica.mixin;

import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardHandler.class})
public class KeyboardHandlerMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"keyPress(JIIII)V"},
      cancellable = true
   )
   private void onKeyPress(long window, int i, int j, int k, int m, CallbackInfo ci) {
      if (i == -1 && j == 310) {
         ((KeyboardHandler)this).keyPress(window, 344, 54, k, m);
         ci.cancel();
      }
   }
}
