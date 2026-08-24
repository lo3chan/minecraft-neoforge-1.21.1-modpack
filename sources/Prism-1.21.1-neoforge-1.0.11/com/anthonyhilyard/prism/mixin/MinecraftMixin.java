package com.anthonyhilyard.prism.mixin;

import com.anthonyhilyard.prism.events.client.RenderTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftMixin {
   @Inject(
      method = {"runTick(Z)V"},
      at = {@At(
         value = "INVOKE_STRING",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
         args = {"ldc=gameRenderer"}
      )},
      require = 1
   )
   public void runTick(boolean tickWorld, CallbackInfo callbackInfo) {
      Minecraft instance = (Minecraft)this;
      RenderTickEvent.START.invoker().onStart(instance.getTimer());
   }
}
