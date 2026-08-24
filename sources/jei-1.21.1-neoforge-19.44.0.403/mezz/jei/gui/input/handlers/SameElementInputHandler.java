package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.Optional;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IMouseOverable;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class SameElementInputHandler implements IUserInputHandler {
   private final IUserInputHandler handler;
   private final IMouseOverable mouseOverable;

   public SameElementInputHandler(IUserInputHandler handler, IMouseOverable mouseOverable) {
      this.handler = handler;
      this.mouseOverable = mouseOverable;
   }

   @Override
   public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      double mouseX = input.getMouseX();
      double mouseY = input.getMouseY();
      return this.mouseOverable.isMouseOver(mouseX, mouseY) ? this.handler.handleUserInput(screen, input, keyBindings).map(handled -> this) : Optional.empty();
   }

   @Override
   public void unfocus() {
      this.handler.unfocus();
   }

   @Override
   public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      return this.mouseOverable.isMouseOver(mouseX, mouseY) ? this.handler.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY) : Optional.empty();
   }

   @Override
   public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      return this.mouseOverable.isMouseOver(mouseX, mouseY)
         ? this.handler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY).map(handled -> this)
         : Optional.empty();
   }
}
