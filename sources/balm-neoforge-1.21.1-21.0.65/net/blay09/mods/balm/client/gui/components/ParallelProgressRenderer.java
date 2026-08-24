package net.blay09.mods.balm.client.gui.components;

import java.util.Arrays;
import net.minecraft.client.gui.GuiGraphics;

public class ParallelProgressRenderer implements ProgressRenderer {
   private final ProgressRenderer[] renderers;
   private final int length;

   public ParallelProgressRenderer(ProgressRenderer... renderers) {
      this.renderers = renderers;
      this.length = Arrays.stream(renderers).mapToInt(ProgressRenderer::getLength).max().orElse(0);
   }

   @Override
   public int getLength() {
      return this.length;
   }

   @Override
   public void render(GuiGraphics guiGraphics, int x, int y, float progress) {
      for (ProgressRenderer renderer : this.renderers) {
         renderer.render(guiGraphics, x, y, progress);
      }
   }
}
