package net.irisshaders.iris.mixin.state_tracking;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderTarget.class})
public class MixinRenderTarget {
   @Inject(
      method = {"bindWrite(Z)V"},
      at = {@At("RETURN")}
   )
   private void iris$onBindFramebuffer(boolean bl, CallbackInfo ci) {
      boolean mainBound = this == Minecraft.getInstance().getMainRenderTarget();
      Iris.getPipelineManager().getPipeline().ifPresent(pipeline -> pipeline.setIsMainBound(mainBound));
   }
}
