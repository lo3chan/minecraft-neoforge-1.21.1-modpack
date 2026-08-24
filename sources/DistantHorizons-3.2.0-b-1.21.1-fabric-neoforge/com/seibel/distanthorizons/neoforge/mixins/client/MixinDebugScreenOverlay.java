package com.seibel.distanthorizons.neoforge.mixins.client;

import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import java.util.List;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DebugScreenOverlay.class})
public class MixinDebugScreenOverlay {
   @Inject(
      method = {"getSystemInformation"},
      at = {@At("RETURN")}
   )
   private void addCustomF3(CallbackInfoReturnable<List<String>> cir) {
      List<String> messages = (List<String>)cir.getReturnValue();
      F3Screen.addStringToDisplay(messages);
   }
}
