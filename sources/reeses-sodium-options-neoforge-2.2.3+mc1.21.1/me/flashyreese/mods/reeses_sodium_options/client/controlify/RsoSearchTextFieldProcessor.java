package me.flashyreese.mods.reeses_sodium_options.client.controlify;

import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ComponentProcessor;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour;
import dev.isxander.controlify.screenop.keyboard.InputTarget;
import dev.isxander.controlify.screenop.keyboard.KeyboardLayouts;
import dev.isxander.controlify.screenop.keyboard.KeyboardOverlayScreen;
import dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour.Handled;
import me.flashyreese.mods.reeses_sodium_options.client.gui.search.SearchTextFieldWidget;

final class RsoSearchTextFieldProcessor implements ComponentProcessor {
   private final SearchTextFieldWidget textField;

   RsoSearchTextFieldProcessor(SearchTextFieldWidget textField) {
      this.textField = textField;
   }

   public ComponentKeyboardBehaviour getKeyboardBehaviour(ScreenProcessor<?> screen, ControllerEntity controller) {
      int keyboardWidth = (int)(screen.screen.width * 0.8F);
      int keyboardHeight = (int)(screen.screen.height * 0.4F);
      return new Handled(
         KeyboardLayouts.simple(),
         new RsoSearchTextFieldProcessor.TextFieldInputTarget(this.textField),
         KeyboardOverlayScreen.aboveOrBelowWidgetPositioner(keyboardWidth, keyboardHeight, 1, this.textField::getRectangle)
      );
   }

   public boolean shouldKeepFocusOnKeyboardMode(ScreenProcessor<?> screen) {
      return true;
   }

   private record TextFieldInputTarget(SearchTextFieldWidget textField) implements InputTarget {
      public boolean supportsCharInput() {
         return true;
      }

      public boolean acceptChar(char ch, int modifiers) {
         return this.textField.rso$acceptChar(ch, modifiers);
      }

      public boolean supportsKeyCodeInput() {
         return true;
      }

      public boolean acceptKeyCode(int keycode, int scancode, int modifiers) {
         return this.textField.rso$acceptKeyCode(keycode, scancode, modifiers);
      }

      public boolean supportsCursorMovement() {
         return true;
      }

      public boolean moveCursor(int amount) {
         return this.textField.rso$moveCursor(amount);
      }

      public boolean supportsCopying() {
         return true;
      }

      public boolean copy() {
         return this.textField.rso$copyText();
      }
   }
}
