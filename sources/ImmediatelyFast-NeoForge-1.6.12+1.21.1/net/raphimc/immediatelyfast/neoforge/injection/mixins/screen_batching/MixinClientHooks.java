package net.raphimc.immediatelyfast.neoforge.injection.mixins.screen_batching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ClientHooks;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ClientHooks.class})
public abstract class MixinClientHooks {
   @WrapOperation(
      method = {"drawScreen", "lambda$drawScreen$1"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreenInternal(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
      )}
   )
   private static void screenBatching(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
      boolean batchScreen = screen instanceof ChatScreen;
      if (ImmediatelyFast.runtimeConfig.experimental_screen_batching && batchScreen) {
         BatchingBuffers.runBatched(guiGraphics, () -> original.call(new Object[]{screen, guiGraphics, mouseX, mouseY, partialTick}));
      } else {
         original.call(new Object[]{screen, guiGraphics, mouseX, mouseY, partialTick});
      }
   }
}
