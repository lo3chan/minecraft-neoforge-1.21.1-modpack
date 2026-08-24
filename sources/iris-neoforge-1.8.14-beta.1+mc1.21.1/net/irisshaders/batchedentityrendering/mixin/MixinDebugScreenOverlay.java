package net.irisshaders.batchedentityrendering.mixin;

import java.util.List;
import net.irisshaders.batchedentityrendering.impl.BatchingDebugMessageHelper;
import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {DebugScreenOverlay.class},
   priority = 1010
)
public abstract class MixinDebugScreenOverlay {
   @Inject(
      method = {"getGameInformation"},
      at = {@At("RETURN")}
   )
   private void batchedentityrendering$appendStats(CallbackInfoReturnable<List<String>> cir) {
      List<String> messages = (List<String>)cir.getReturnValue();
      DrawCallTrackingRenderBuffers drawTracker = (DrawCallTrackingRenderBuffers)Minecraft.getInstance().renderBuffers();
      if (Iris.getIrisConfig().areDebugOptionsEnabled()) {
         messages.add("");
         messages.add("[Entity Batching] " + BatchingDebugMessageHelper.getDebugMessage(drawTracker));
      }
   }
}
