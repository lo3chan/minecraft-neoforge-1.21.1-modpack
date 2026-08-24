package mezz.jei.common.gui;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.function.Supplier;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class OffsetJeiInputHandler implements IJeiInputHandler {
   private final IJeiInputHandler inputHandler;
   private final Supplier<ScreenPosition> offset;

   public OffsetJeiInputHandler(IJeiInputHandler inputHandler, Supplier<ScreenPosition> offset) {
      this.inputHandler = inputHandler;
      this.offset = offset;
   }

   @Override
   public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
      ScreenPosition screenPosition = this.offset.get();
      double offsetMouseX = mouseX - screenPosition.x();
      double offsetMouseY = mouseY - screenPosition.y();
      ScreenRectangle originalArea = this.inputHandler.getArea();
      if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
         ScreenPosition position = originalArea.position();
         double relativeMouseX = offsetMouseX - position.x();
         double relativeMouseY = offsetMouseY - position.y();
         return this.inputHandler.handleInput(relativeMouseX, relativeMouseY, input);
      } else {
         return false;
      }
   }

   @Override
   public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      ScreenPosition screenPosition = this.offset.get();
      double offsetMouseX = mouseX - screenPosition.x();
      double offsetMouseY = mouseY - screenPosition.y();
      ScreenRectangle originalArea = this.inputHandler.getArea();
      if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
         ScreenPosition position = originalArea.position();
         double relativeMouseX = offsetMouseX - position.x();
         double relativeMouseY = offsetMouseY - position.y();
         return this.inputHandler.handleMouseScrolled(relativeMouseX, relativeMouseY, scrollDeltaX, scrollDeltaY);
      } else {
         return false;
      }
   }

   @Override
   public boolean handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      ScreenPosition screenPosition = this.offset.get();
      double offsetMouseX = mouseX - screenPosition.x();
      double offsetMouseY = mouseY - screenPosition.y();
      ScreenRectangle originalArea = this.inputHandler.getArea();
      if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
         ScreenPosition position = originalArea.position();
         double relativeMouseX = offsetMouseX - position.x();
         double relativeMouseY = offsetMouseY - position.y();
         return this.inputHandler.handleMouseDragged(relativeMouseX, relativeMouseY, mouseKey, dragX, dragY);
      } else {
         return false;
      }
   }

   @Override
   public void handleMouseMoved(double mouseX, double mouseY) {
      ScreenPosition screenPosition = this.offset.get();
      double offsetMouseX = mouseX - screenPosition.x();
      double offsetMouseY = mouseY - screenPosition.y();
      ScreenRectangle originalArea = this.inputHandler.getArea();
      if (MathUtil.contains(originalArea, offsetMouseX, offsetMouseY)) {
         ScreenPosition position = originalArea.position();
         double relativeMouseX = offsetMouseX - position.x();
         double relativeMouseY = offsetMouseY - position.y();
         this.inputHandler.handleMouseMoved(relativeMouseX, relativeMouseY);
      }
   }

   @Override
   public ScreenRectangle getArea() {
      ScreenRectangle area = this.inputHandler.getArea();
      return new ScreenRectangle(this.offset.get(), area.width(), area.height());
   }
}
