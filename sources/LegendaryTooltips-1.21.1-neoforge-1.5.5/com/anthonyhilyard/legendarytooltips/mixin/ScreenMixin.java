package com.anthonyhilyard.legendarytooltips.mixin;

import com.anthonyhilyard.legendarytooltips.LegendaryTooltips;
import java.util.List;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Screen.class})
public class ScreenMixin extends AbstractContainerEventHandler {
   @Shadow
   public List<? extends GuiEventListener> children() {
      throw new UnsupportedOperationException("Unimplemented method 'children'");
   }

   @Inject(
      method = {"onClose()V"},
      at = {@At("HEAD")}
   )
   public void screenClosed(CallbackInfo info) {
      LegendaryTooltips.scrollTooltipsKeyDown = false;
   }

   @Inject(
      method = {"keyPressed(III)Z"},
      at = {@At("HEAD")}
   )
   public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> info) {
      if (LegendaryTooltips.scrollTooltips.matches(i, j)) {
         LegendaryTooltips.scrollTooltipsKeyDown = true;
      }
   }
}
