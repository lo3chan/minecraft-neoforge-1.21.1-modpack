/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.ReflectionUtil;
import mezz.jei.gui.input.ICharTypedHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.ChatLinkInputHandler;
import mezz.jei.gui.input.handlers.DragRouter;
import mezz.jei.gui.input.handlers.UserInputRouter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;

public class ClientInputHandler {
    private final List<ICharTypedHandler> charTypedHandlers;
    private final ChatLinkInputHandler chatLinkInputHandler;
    private final UserInputRouter inputRouter;
    private final DragRouter dragRouter;
    private final IInternalKeyMappings keybindings;
    private final IScreenHelper screenHelper;
    private final ReflectionUtil reflectionUtil = new ReflectionUtil();

    public ClientInputHandler(List<ICharTypedHandler> charTypedHandlers, ChatLinkInputHandler chatLinkInputHandler, UserInputRouter inputRouter, DragRouter dragRouter, IInternalKeyMappings keybindings, IScreenHelper screenHelper) {
        this.charTypedHandlers = charTypedHandlers;
        this.chatLinkInputHandler = chatLinkInputHandler;
        this.inputRouter = inputRouter;
        this.dragRouter = dragRouter;
        this.keybindings = keybindings;
        this.screenHelper = screenHelper;
    }

    public void onInitGui() {
        this.chatLinkInputHandler.handleGuiChange();
        this.inputRouter.handleGuiChange();
        this.dragRouter.handleGuiChange();
    }

    public boolean onKeyboardKeyPressedPre(Screen screen, UserInput input) {
        if (this.chatLinkInputHandler.handleUserInput(screen, input, this.keybindings)) {
            return true;
        }
        if (!this.isContainerTextFieldFocused(screen) && this.screenHelper.getGuiProperties(screen).isPresent()) {
            return this.inputRouter.handleUserInput(screen, input, this.keybindings);
        }
        return false;
    }

    public boolean onKeyboardKeyPressedPost(Screen screen, UserInput input) {
        if (this.isContainerTextFieldFocused(screen) && this.screenHelper.getGuiProperties(screen).isPresent()) {
            return this.inputRouter.handleUserInput(screen, input, this.keybindings);
        }
        return false;
    }

    public boolean onKeyboardCharTypedPre(Screen screen, char codePoint, int modifiers) {
        if (!this.isContainerTextFieldFocused(screen)) {
            return this.handleCharTyped(codePoint, modifiers);
        }
        return false;
    }

    public void onKeyboardCharTypedPost(Screen screen, char codePoint, int modifiers) {
        if (this.isContainerTextFieldFocused(screen)) {
            this.handleCharTyped(codePoint, modifiers);
        }
    }

    public boolean onGuiMouseClicked(Screen screen, UserInput input) {
        if (this.chatLinkInputHandler.handleUserInput(screen, input, this.keybindings)) {
            return true;
        }
        if (this.screenHelper.getGuiProperties(screen).isEmpty()) {
            return false;
        }
        boolean handled = this.inputRouter.handleUserInput(screen, input, this.keybindings);
        if (Minecraft.getInstance().screen == screen && input.is(this.keybindings.getLeftClick())) {
            handled |= this.dragRouter.startDrag(screen, input);
        }
        return handled;
    }

    public boolean onGuiMouseReleased(Screen screen, UserInput input) {
        if (this.chatLinkInputHandler.handleUserInput(screen, input, this.keybindings)) {
            return true;
        }
        if (this.screenHelper.getGuiProperties(screen).isEmpty()) {
            return false;
        }
        boolean handled = this.inputRouter.handleUserInput(screen, input, this.keybindings);
        if (input.is(this.keybindings.getLeftClick())) {
            handled |= this.dragRouter.completeDrag(screen, input);
        }
        return handled;
    }

    public boolean onGuiMouseScroll(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.inputRouter.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    public boolean onGuiMouseDragged(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY) {
        InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(button);
        return this.inputRouter.handleMouseDragged(mouseX, mouseY, input, dragX, dragY);
    }

    private boolean handleCharTyped(char codePoint, int modifiers) {
        return this.charTypedHandlers.stream().filter(ICharTypedHandler::hasKeyboardFocus).anyMatch(handler -> handler.onCharTyped(codePoint, modifiers));
    }

    private boolean isContainerTextFieldFocused(Screen screen) {
        return this.reflectionUtil.getFieldWithClass(screen, EditBox.class).anyMatch(textField -> textField.isActive() && textField.isFocused());
    }
}

