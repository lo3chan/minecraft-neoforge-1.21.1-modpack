package mezz.jei.gui.input;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.Optional;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.common.input.KeyNameUtil;
import mezz.jei.common.platform.IPlatformInputHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.util.StringUtil;

public class UserInput implements IJeiUserInput {
   private final Key key;
   private final double mouseX;
   private final double mouseY;
   private final int modifiers;
   private final InputType inputType;

   public static UserInput fromVanilla(int keyCode, int scanCode, int modifiers, InputType inputType) {
      Key input = InputConstants.getKey(keyCode, scanCode);
      return new UserInput(input, MouseUtil.getX(), MouseUtil.getY(), modifiers, inputType);
   }

   public static Optional<UserInput> fromVanilla(double mouseX, double mouseY, int mouseButton, InputType inputType) {
      if (mouseButton < 0) {
         return Optional.empty();
      } else {
         Key input = Type.MOUSE.getOrCreate(mouseButton);
         UserInput userInput = new UserInput(input, mouseX, mouseY, 0, inputType);
         return Optional.of(userInput);
      }
   }

   public UserInput(Key key, double mouseX, double mouseY, int modifiers, InputType inputType) {
      this.key = key;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.modifiers = modifiers;
      this.inputType = inputType;
   }

   @Override
   public Key getKey() {
      return this.key;
   }

   public double getMouseX() {
      return this.mouseX;
   }

   public double getMouseY() {
      return this.mouseY;
   }

   public InputType getInputType() {
      return this.inputType;
   }

   @Override
   public int getModifiers() {
      return this.modifiers;
   }

   @Override
   public boolean isSimulate() {
      return this.inputType == InputType.SIMULATE;
   }

   private boolean isKeyboard() {
      return this.key.getType() == Type.KEYSYM;
   }

   public boolean isAllowedChatCharacter() {
      return this.isKeyboard() && StringUtil.isAllowedChatCharacter((char)this.key.getValue());
   }

   @Override
   public boolean is(IJeiKeyMapping keyMapping) {
      return keyMapping.isActiveAndMatches(this.key);
   }

   @Override
   public boolean is(KeyMapping keyMapping) {
      IPlatformInputHelper inputHelper = Services.PLATFORM.getInputHelper();
      return inputHelper.isActiveAndMatches(keyMapping, this.key);
   }

   public boolean callVanilla(IMouseOverable mouseOverable, UserInput.MouseClickable mouseClickable) {
      if (this.key.getType() != Type.MOUSE || !mouseOverable.isMouseOver(this.mouseX, this.mouseY)) {
         return false;
      } else {
         return this.isSimulate() ? true : mouseClickable.mouseClicked(this.mouseX, this.mouseY, this.key.getValue());
      }
   }

   public boolean callVanilla(UserInput.KeyPressable keyPressable) {
      if (this.key.getType() == Type.KEYSYM) {
         return this.isSimulate() ? false : keyPressable.keyPressed(this.key.getValue(), 0, this.modifiers);
      } else {
         return false;
      }
   }

   public boolean callVanilla(IMouseOverable mouseOverable, UserInput.MouseClickable mouseClickable, UserInput.KeyPressable keyPressable) {
      return switch (this.key.getType()) {
         case KEYSYM -> this.callVanilla(keyPressable);
         case MOUSE -> this.callVanilla(mouseOverable, mouseClickable);
         default -> false;
      };
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("inputType", this.inputType)
         .add("key", KeyNameUtil.getKeyDisplayName(this.key).getString())
         .add("modifiers", this.modifiers)
         .add("mouse", String.format("%s, %s", this.mouseX, this.mouseY))
         .toString();
   }

   @FunctionalInterface
   public interface KeyPressable {
      boolean keyPressed(int var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface MouseClickable {
      boolean mouseClicked(double var1, double var3, int var5);
   }
}
