package com.alonie.brbe.interfaces;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface TopLayerOverlayProvider {
   boolean brbe$hasTopLayerOverlay();

   void brbe$renderTopLayerOverlay(GuiGraphics var1, int var2, int var3, float var4);

   boolean brbe$clickTopLayerOverlay(double var1, double var3, int var5);

   ScreenRectangle brbe$getTopLayerOverlayBounds();
}
