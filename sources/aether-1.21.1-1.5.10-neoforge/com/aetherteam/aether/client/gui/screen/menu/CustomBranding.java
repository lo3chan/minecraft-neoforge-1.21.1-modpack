package com.aetherteam.aether.client.gui.screen.menu;

import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphics;

public interface CustomBranding {
   boolean forEachLineBranding(boolean var1, boolean var2, BiConsumer<Integer, String> var3, GuiGraphics var4, int var5);

   boolean forEachAboveCopyrightLineBranding(BiConsumer<Integer, String> var1, GuiGraphics var2, int var3);
}
