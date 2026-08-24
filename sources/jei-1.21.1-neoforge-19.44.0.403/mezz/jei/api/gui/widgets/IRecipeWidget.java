package mezz.jei.api.gui.widgets;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;

public interface IRecipeWidget {
   ScreenPosition getPosition();

   default void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
      ScreenPosition position = this.getPosition();
      this.draw(guiGraphics, mouseX + position.x(), mouseY + position.y());
   }

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   default void draw(GuiGraphics guiGraphics, double mouseX, double mouseY) {
   }

   default void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
   }

   default void tick() {
   }
}
