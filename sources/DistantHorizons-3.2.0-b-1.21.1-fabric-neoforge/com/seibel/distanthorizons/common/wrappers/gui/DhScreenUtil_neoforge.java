package com.seibel.distanthorizons.common.wrappers.gui;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class DhScreenUtil_neoforge {
   public static void setScreen(Screen screen) {
      Objects.requireNonNull(Minecraft.getInstance()).setScreen(screen);
   }
}
