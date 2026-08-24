package mezz.jei.api.gui.inputs;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface IJeiGuiEventListener {
   ScreenRectangle getArea();

   default void mouseMoved(double mouseX, double mouseY) {
   }

   default boolean mouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean mouseReleased(double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      return false;
   }

   default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return false;
   }

   default boolean keyPressed(double mouseX, double mouseY, int keyCode, int scanCode, int modifiers) {
      return false;
   }
}
