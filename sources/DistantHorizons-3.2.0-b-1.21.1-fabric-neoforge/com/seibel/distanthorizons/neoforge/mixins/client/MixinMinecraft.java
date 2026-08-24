package com.seibel.distanthorizons.neoforge.mixins.client;

import com.seibel.distanthorizons.common.commonMixins.DhUpdateScreenBase_neoforge;
import com.seibel.distanthorizons.core.jar.updater.SelfUpdater;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Redirect(
      method = {"Lnet/minecraft/client/Minecraft;onGameLoadFinished(Lnet/minecraft/client/Minecraft$GameLoadCookie;)V"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/Runnable;run()V"
      )
   )
   private void buildInitialScreens(Runnable runnable) {
      DhUpdateScreenBase_neoforge.tryShowUpdateScreenAndRunAutoUpdateStartup(runnable);
      runnable.run();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"},
      remap = false
   )
   public void close(CallbackInfo ci) {
      SelfUpdater.onClose();
   }
}
