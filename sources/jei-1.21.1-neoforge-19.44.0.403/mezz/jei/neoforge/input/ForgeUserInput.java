package mezz.jei.neoforge.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.Optional;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.MouseUtil;
import mezz.jei.gui.input.UserInput;
import net.neoforged.neoforge.client.event.ScreenEvent.KeyPressed;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased;

public final class ForgeUserInput {
   private ForgeUserInput() {
   }

   public static UserInput fromEvent(KeyPressed keyEvent) {
      Key input = InputConstants.getKey(keyEvent.getKeyCode(), keyEvent.getScanCode());
      double mouseX = MouseUtil.getX();
      double mouseY = MouseUtil.getY();
      int modifiers = keyEvent.getModifiers();
      return new UserInput(input, mouseX, mouseY, modifiers, InputType.IMMEDIATE);
   }

   public static Optional<UserInput> fromEvent(MouseButtonPressed event) {
      int button = event.getButton();
      if (button < 0) {
         return Optional.empty();
      } else {
         Key input = Type.MOUSE.getOrCreate(button);
         UserInput userInput = new UserInput(input, event.getMouseX(), event.getMouseY(), 0, InputType.SIMULATE);
         return Optional.of(userInput);
      }
   }

   public static Optional<UserInput> fromEvent(MouseButtonReleased event) {
      int button = event.getButton();
      if (button < 0) {
         return Optional.empty();
      } else {
         Key input = Type.MOUSE.getOrCreate(button);
         UserInput userInput = new UserInput(input, event.getMouseX(), event.getMouseY(), 0, InputType.EXECUTE);
         return Optional.of(userInput);
      }
   }
}
