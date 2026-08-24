package jeresources.jei.worldgen;

import java.util.LinkedList;
import java.util.List;
import jeresources.entry.WorldGenEntry;
import jeresources.util.Font;
import jeresources.util.RenderHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class WorldGenWrapper implements IRecipeCategoryExtension<WorldGenEntry> {
   protected static final int X_OFFSET = 29;
   protected static final int Y_OFFSET = 52;
   protected static final int X_AXIS_SIZE = 128;
   protected static final int Y_AXIS_SIZE = 40;
   protected static final String ORE_SLOT_NAME = "oreSlot";

   public void drawInfo(WorldGenEntry recipe, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      float[] array = recipe.getChances();
      double max = 0.0;
      float[] xPrev = array;
      int var13 = array.length;

      for (int yPrev = 0; yPrev < var13; yPrev++) {
         double d = xPrev[yPrev];
         if (d > max) {
            max = d;
         }
      }

      double xPrevx = 29.0;
      double yPrevx = 52.0;
      double space = 128.0 / ((array.length - 1) * 1.0);

      for (int i = 0; i < array.length; i++) {
         double value = array[i];
         double y = 52.0 - value / max * 40.0;
         if (i > 0) {
            double x = xPrevx + space;
            RenderHelper.drawLine(guiGraphics, (int)xPrevx, (int)yPrevx, (int)x, (int)y, recipe.getColour());
            xPrevx = x;
         }

         yPrevx = y;
      }

      int xPercents = 27;
      int yPercents = 48;
      String minPercent = "0%";
      int minPercentWidth = Font.small.getStringWidth("0%");
      Font.small.print(guiGraphics, "0%", 27 - minPercentWidth, 48);
      String maxPercent = String.format("%.2f", max * 100.0) + "%";
      int maxPercentWidth = Font.small.getStringWidth(maxPercent);
      Font.small.print(guiGraphics, maxPercent, 27 - maxPercentWidth, 8);
      int yLabels = 54;
      int xLabels = 29;
      int minLabel = recipe.getMinY();
      int minLabelWidth = Font.small.getStringWidth(String.valueOf(minLabel));
      int minLabelOffset = 29 - minLabelWidth / 2;
      Font.small.print(guiGraphics, minLabel, minLabelOffset, 54);
      int maxLabel = recipe.getMaxY();
      int maxLabelWidth = Font.small.getStringWidth(String.valueOf(maxLabel));
      int maxLabelOffset = 157 - maxLabelWidth / 2;
      Font.small.print(guiGraphics, maxLabel, maxLabelOffset, 54);
      int midLabel = (maxLabel + minLabel) / 2;
      int midLabelWidth = Font.small.getStringWidth(String.valueOf(midLabel));
      int midLabelOffset = 93 - midLabelWidth / 2;
      Font.small.print(guiGraphics, midLabel, midLabelOffset, 54);
      Font.small.print(guiGraphics, TranslationHelper.translateAndFormat("jer.worldgen.drops"), 6, 59);
      String dimension = TranslationHelper.tryDimensionTranslate(recipe.getDimension());
      int x = (recipeWidth - Font.normal.getStringWidth(dimension)) / 2;
      Font.normal.print(guiGraphics, dimension, x, 0);
   }

   public List<Component> getTooltipStrings(WorldGenEntry recipe, double mouseX, double mouseY) {
      List<Component> tooltip = new LinkedList<>();
      if (this.onGraph(mouseX, mouseY)) {
         tooltip = this.getLineTooltip(recipe, mouseX, tooltip);
      }

      return tooltip;
   }

   private List<Component> getLineTooltip(WorldGenEntry recipe, double mouseX, List<Component> tooltip) {
      double exactMouseX = getExactMouseX(mouseX);
      float[] chances = recipe.getChances();
      double space = 128.0 / (chances.length * 1.0);
      int index = (int)((exactMouseX - 29.0 + 1.0) / space);
      int yValue = index + recipe.getMinY();
      if (index >= 0 && index < chances.length) {
         float chance = chances[index] * 100.0F;
         String percent = !(chance > 0.01F) && chance != 0.0F ? " <0.01%" : String.format(" (%.2f%%)", chance);
         tooltip.add(Component.literal("Y: " + yValue + percent));
      }

      return tooltip;
   }

   private static double getExactMouseX(double mouseX) {
      Minecraft mc = Minecraft.getInstance();
      int scaledWidth = mc.getWindow().getGuiScaledWidth();
      double mouseXExact = mc.mouseHandler.xpos() * scaledWidth / mc.getWindow().getWidth();
      double mouseXFraction = mouseXExact - Math.floor(mouseXExact);
      return mouseX + mouseXFraction;
   }

   private boolean onGraph(double mouseX, double mouseY) {
      return mouseX >= 28.0 && mouseX < 157.0 && mouseY >= 11.0 && mouseY < 52.0;
   }
}
