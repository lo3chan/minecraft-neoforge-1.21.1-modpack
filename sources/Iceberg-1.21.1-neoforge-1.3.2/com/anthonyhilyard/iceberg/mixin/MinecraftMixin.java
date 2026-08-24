package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.events.client.RenderTickEvents;
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
      )}
   )
   public void runTick(boolean tickWorld, CallbackInfo callbackInfo) {
      Minecraft instance = (Minecraft)this;
      RenderTickEvents.START.invoker().onStart(instance.getTimer());
   }
}
