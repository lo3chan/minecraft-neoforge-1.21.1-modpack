package mezz.jei.gui.input.handlers;

import java.util.Optional;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.TextHistory;
import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class TextFieldInputHandler implements IUserInputHandler {
   private final GuiTextFieldFilter textFieldFilter;

   public TextFieldInputHandler(GuiTextFieldFilter textFieldFilter) {
      this.textFieldFilter = textFieldFilter;
   }

   @Override
   public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      return this.handleUserInputBoolean(input, keyBindings) ? Optional.of(this) : Optional.empty();
   }

   private boolean handleUserInputBoolean(UserInput input, IInternalKeyMappings keyBindings) {
      if (input.is(keyBindings.getEnterKey()) || input.is(keyBindings.getEscapeKey())) {
         return this.handleSetFocused(input, false);
      } else if (input.is(keyBindings.getFocusSearch())) {
         return this.handleSetFocused(input, true);
      } else if (input.is(keyBindings.getHoveredClearSearchBar()) && this.textFieldFilter.isMouseOver(input.getMouseX(), input.getMouseY())) {
         return this.handleHoveredClearSearchBar(input);
      } else if (input.callVanilla(this.textFieldFilter::isMouseOver, this.textFieldFilter::mouseClicked, this.textFieldFilter::keyPressed)) {
         this.handleSetFocused(input, true);
         return true;
      } else if (input.is(keyBindings.getPreviousSearch())) {
         return this.handleNavigateHistory(input, TextHistory.Direction.PREVIOUS);
      } else {
         return input.is(keyBindings.getNextSearch())
            ? this.handleNavigateHistory(input, TextHistory.Direction.NEXT)
            : this.textFieldFilter.canConsumeInput() && input.isAllowedChatCharacter();
      }
   }

   private boolean handleSetFocused(UserInput input, boolean focused) {
      if (this.textFieldFilter.isFocused() != focused) {
         if (!input.isSimulate()) {
            this.textFieldFilter.setFocused(focused);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean handleHoveredClearSearchBar(UserInput input) {
      if (!input.isSimulate()) {
         this.textFieldFilter.setValue("");
         this.textFieldFilter.setFocused(true);
      }

      return true;
   }

   private boolean handleNavigateHistory(UserInput input, TextHistory.Direction direction) {
      return this.textFieldFilter.isFocused() ? this.textFieldFilter.getHistory(direction).map(newText -> {
         if (!input.isSimulate()) {
            this.textFieldFilter.setValue(newText);
         }

         return true;
      }).orElse(false) : false;
   }

   @Override
   public void unfocus() {
      this.textFieldFilter.setFocused(false);
   }
}
