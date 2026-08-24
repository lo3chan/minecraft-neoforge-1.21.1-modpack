package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.core.config.gui.AbstractScreen;
import net.minecraft.class_437;

public class MinecraftScreen_fabric {
   public static class_437 getScreen(class_437 parent, AbstractScreen screen, String translationName) {
      return new MinecraftScreen$ConfigScreenRenderer_fabric(parent, screen, translationName);
   }
}
