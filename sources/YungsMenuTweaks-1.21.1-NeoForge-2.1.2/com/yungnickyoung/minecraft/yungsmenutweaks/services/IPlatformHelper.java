package com.yungnickyoung.minecraft.yungsmenutweaks.services;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public interface IPlatformHelper {
   String getPlatformName();

   boolean isModLoaded(String var1);

   boolean isDevelopmentEnvironment();

   void renderBackground(Screen var1, GuiGraphics var2, ResourceLocation var3);
}
