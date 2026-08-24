package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Uniform.class})
public class MixinUniform {
   @Inject(
      method = {"glGetUniformLocation"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void iris$glGetUniformLocation(int programId, CharSequence name, CallbackInfoReturnable<Integer> cir) {
      int location = (Integer)cir.getReturnValue();
      if (location == -1 && name.equals("Sampler0")) {
         location = GlStateManager._glGetUniformLocation(programId, "tex");
         if (location == -1) {
            location = GlStateManager._glGetUniformLocation(programId, "gtexture");
            if (location == -1) {
               location = GlStateManager._glGetUniformLocation(programId, "texture");
            }
         }
      }

      if ((Integer)cir.getReturnValue() == -1 && location != -1) {
         cir.setReturnValue(location);
      }
   }
}
