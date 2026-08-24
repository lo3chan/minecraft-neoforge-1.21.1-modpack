package mezz.jei.api.gui.ingredient;

import java.util.List;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IRecipeSlotDrawable extends IRecipeSlotView {
   @Deprecated(
      since = "19.34.0",
      forRemoval = true
   )
   void draw(GuiGraphics var1);

   default void draw(GuiGraphics guiGraphics, boolean hovered) {
      this.draw(guiGraphics);
      if (hovered) {
         this.drawHoverOverlays(guiGraphics);
      }
   }

   @Deprecated(
      since = "19.34.0",
      forRemoval = true
   )
   void drawHoverOverlays(GuiGraphics var1);

   @Deprecated(
      since = "19.22.0",
      forRemoval = true
   )
   List<Component> getTooltip();

   @Deprecated(
      since = "19.22.0",
      forRemoval = true
   )
   void getTooltip(ITooltipBuilder var1);

   void drawTooltip(GuiGraphics var1, int var2, int var3);

   boolean isMouseOver(double var1, double var3);

   void setPosition(int var1, int var2);

   IIngredientConsumer createDisplayOverrides();

   void clearDisplayOverrides();

   @Deprecated(
      since = "19.6.0",
      forRemoval = true
   )
   Rect2i getRect();

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   default void addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
   }

   Rect2i getAreaIncludingBackground();
}
