package net.irisshaders.iris.mixin.vertices;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VertexFormatElement.class})
public class MixinVertexFormatElement {
   @Inject(
      method = {"supportsUsage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void iris$fixGenericAttributes(int index, Usage type, CallbackInfoReturnable<Boolean> cir) {
      if (type == Usage.GENERIC) {
         cir.setReturnValue(true);
      }
   }
}
