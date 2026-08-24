package com.seibel.distanthorizons.core.config.gui;

import java.nio.file.Path;
import java.util.List;

public abstract class AbstractScreen {
   public long minecraftWindow;
   public int width;
   public int height;
   public int scaledWidth;
   public int scaledHeight;
   public int mouseX = 0;
   public int mouseY = 0;
   public boolean shouldCloseOnEsc = true;
   public boolean close = false;

   public abstract void init();

   public abstract void render(float f);

   public void tick() {
   }

   public void onResize() {
   }

   public void onClose() {
   }

   public void onFilesDrop(List<Path> files) {
   }
}
