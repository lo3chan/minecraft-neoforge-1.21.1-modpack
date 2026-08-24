package net.raphimc.immediatelyfast.injection.mixins.screen_batching.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatScreen.class})
public abstract class MixinChatScreen {
   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/CommandSuggestions;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"
      )}
   )
   private void forceDraw(GuiGraphics drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (drawContext.bufferSource instanceof BatchableBufferSource) {
         drawContext.flush();
      }
   }
}
