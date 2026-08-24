package net.blay09.mods.balm.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;

public interface ProgressRenderer {
   int getLength();

   void render(GuiGraphics var1, int var2, int var3, float var4);
}
