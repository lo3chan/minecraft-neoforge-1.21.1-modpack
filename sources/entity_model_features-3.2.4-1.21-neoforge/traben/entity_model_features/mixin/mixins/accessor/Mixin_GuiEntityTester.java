package traben.entity_model_features.mixin.mixins.accessor;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin({GameRenderer.class})
public class Mixin_GuiEntityTester {
   @ModifyArg(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"
      )
   )
   private String etf$beforeRenderToTexture(String string) {
      if (string.equals("gui")) {
         EMFAnimationEntityContext.setIsInGui = true;
      }

      return string;
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void etf$afterRenderToTexture(CallbackInfo ci) {
      EMFAnimationEntityContext.setIsInGui = false;
   }
}
