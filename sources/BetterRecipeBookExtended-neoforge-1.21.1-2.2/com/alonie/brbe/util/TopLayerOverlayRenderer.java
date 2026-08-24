package com.alonie.brbe.util;

import com.alonie.brbe.interfaces.TopLayerOverlayProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;

public final class TopLayerOverlayRenderer {
   private TopLayerOverlayRenderer() {
   }

   public static boolean hasOverlay(Screen screen) {
      return screen instanceof TopLayerOverlayProvider provider ? provider.brbe$hasTopLayerOverlay() : false;
   }

   public static void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (screen instanceof TopLayerOverlayProvider provider && provider.brbe$hasTopLayerOverlay()) {
         provider.brbe$renderTopLayerOverlay(guiGraphics, mouseX, mouseY, partialTick);
      }
   }

   public static ScreenRectangle getOverlayBounds(Screen screen) {
      return screen instanceof TopLayerOverlayProvider provider ? provider.brbe$getTopLayerOverlayBounds() : null;
   }
}
