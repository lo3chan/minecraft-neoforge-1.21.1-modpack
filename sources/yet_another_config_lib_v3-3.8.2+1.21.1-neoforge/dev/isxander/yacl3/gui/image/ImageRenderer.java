package dev.isxander.yacl3.gui.image;

import net.minecraft.client.gui.GuiGraphics;

public interface ImageRenderer {
   int render(GuiGraphics var1, int var2, int var3, int var4, float var5);

   void close();

   default void tick() {
   }
}
