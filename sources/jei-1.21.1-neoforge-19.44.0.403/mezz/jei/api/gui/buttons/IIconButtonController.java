package mezz.jei.api.gui.buttons;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

public interface IIconButtonController {
   boolean onPress(IJeiUserInput var1);

   default void getTooltips(ITooltipBuilder tooltip) {
   }

   default void initState(IButtonState state) {
      this.updateState(state);
   }

   default void updateState(IButtonState state) {
   }

   default void drawExtras(GuiGraphics guiGraphics, Rect2i buttonArea, int mouseX, int mouseY, float partialTicks) {
   }
}
