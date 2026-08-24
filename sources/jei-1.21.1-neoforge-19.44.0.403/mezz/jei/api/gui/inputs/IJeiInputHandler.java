package mezz.jei.api.gui.inputs;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface IJeiInputHandler {
   ScreenRectangle getArea();

   default boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
      return false;
   }

   default boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      return false;
   }

   default boolean handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      return false;
   }

   default void handleMouseMoved(double mouseX, double mouseY) {
   }
}
