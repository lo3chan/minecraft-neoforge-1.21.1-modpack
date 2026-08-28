/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.isxander.controlify.controller.ControllerEntity
 *  dev.isxander.controlify.screenop.ComponentProcessor
 *  dev.isxander.controlify.screenop.ScreenProcessor
 *  dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour
 *  dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour$Handled
 *  dev.isxander.controlify.screenop.keyboard.InputTarget
 *  dev.isxander.controlify.screenop.keyboard.KeyboardLayouts
 *  dev.isxander.controlify.screenop.keyboard.KeyboardOverlayScreen
 */
package me.flashyreese.mods.reeses_sodium_options.client.controlify;

import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ComponentProcessor;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour;
import dev.isxander.controlify.screenop.keyboard.InputTarget;
import dev.isxander.controlify.screenop.keyboard.KeyboardLayouts;
import dev.isxander.controlify.screenop.keyboard.KeyboardOverlayScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.search.SearchTextFieldWidget;

final class RsoSearchTextFieldProcessor
implements ComponentProcessor {
    private final SearchTextFieldWidget textField;

    RsoSearchTextFieldProcessor(SearchTextFieldWidget textField) {
        this.textField = textField;
    }

    public ComponentKeyboardBehaviour getKeyboardBehaviour(ScreenProcessor<?> screen, ControllerEntity controller) {
        int keyboardWidth = (int)((float)screen.screen.width * 0.8f);
        int keyboardHeight = (int)((float)screen.screen.height * 0.4f);
        return new ComponentKeyboardBehaviour.Handled(KeyboardLayouts.simple(), (InputTarget)new TextFieldInputTarget(this.textField), KeyboardOverlayScreen.aboveOrBelowWidgetPositioner((int)keyboardWidth, (int)keyboardHeight, (int)1, this.textField::getRectangle));
    }

    public boolean shouldKeepFocusOnKeyboardMode(ScreenProcessor<?> screen) {
        return true;
    }

    private record TextFieldInputTarget(SearchTextFieldWidget textField) implements InputTarget
    {
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

