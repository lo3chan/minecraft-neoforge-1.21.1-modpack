package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.core.config.gui.AbstractScreen;
import net.minecraft.client.gui.screens.Screen;

public class MinecraftScreen_neoforge {
   public static Screen getScreen(Screen parent, AbstractScreen screen, String translationName) {
      return new MinecraftScreen$ConfigScreenRenderer_neoforge(parent, screen, translationName);
   }
}
