package com.seibel.distanthorizons.fabric.mixins.client;

import com.seibel.distanthorizons.common.commonMixins.DhUpdateScreenBase_fabric;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_310.class})
public abstract class MixinMinecraft {
   @Unique
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   @Unique
   private class_638 lastLevel;

   @Redirect(
      method = {"Lnet/minecraft/client/Minecraft;onGameLoadFinished(Lnet/minecraft/client/Minecraft$GameLoadCookie;)V"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/Runnable;run()V"
      )
   )
   private void buildInitialScreens(Runnable runnable) {
      DhUpdateScreenBase_fabric.tryShowUpdateScreenAndRunAutoUpdateStartup(runnable);
      runnable.run();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   public void close(CallbackInfo ci) {
      SelfUpdater.onClose();
   }
}
