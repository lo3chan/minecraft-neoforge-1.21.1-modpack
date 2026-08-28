/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import java.util.Optional;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.TextHistory;
import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class TextFieldInputHandler
implements IUserInputHandler {
    private final GuiTextFieldFilter textFieldFilter;

    public TextFieldInputHandler(GuiTextFieldFilter textFieldFilter) {
        this.textFieldFilter = textFieldFilter;
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (this.handleUserInputBoolean(input, keyBindings)) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    private boolean handleUserInputBoolean(UserInput input, IInternalKeyMappings keyBindings) {
        if (input.is(keyBindings.getEnterKey()) || input.is(keyBindings.getEscapeKey())) {
            return this.handleSetFocused(input, false);
        }
        if (input.is(keyBindings.getFocusSearch())) {
            return this.handleSetFocused(input, true);
        }
        if (input.is(keyBindings.getHoveredClearSearchBar()) && this.textFieldFilter.isMouseOver(input.getMouseX(), input.getMouseY())) {
            return this.handleHoveredClearSearchBar(input);
        }
        if (input.callVanilla(this.textFieldFilter::isMouseOver, (arg_0, arg_1, arg_2) -> ((GuiTextFieldFilter)this.textFieldFilter).mouseClicked(arg_0, arg_1, arg_2), (arg_0, arg_1, arg_2) -> ((GuiTextFieldFilter)this.textFieldFilter).keyPressed(arg_0, arg_1, arg_2))) {
            this.handleSetFocused(input, true);
            return true;
        }
        if (input.is(keyBindings.getPreviousSearch())) {
            return this.handleNavigateHistory(input, TextHistory.Direction.PREVIOUS);
        }
        if (input.is(keyBindings.getNextSearch())) {
            return this.handleNavigateHistory(input, TextHistory.Direction.NEXT);
        }
        return this.textFieldFilter.canConsumeInput() && input.isAllowedChatCharacter();
    }

    private boolean handleSetFocused(UserInput input, boolean focused) {
        if (this.textFieldFilter.isFocused() != focused) {
            if (!input.isSimulate()) {
                this.textFieldFilter.setFocused(focused);
            }
            return true;
        }
        return false;
    }

    private boolean handleHoveredClearSearchBar(UserInput input) {
        if (!input.isSimulate()) {
            this.textFieldFilter.setValue("");
            this.textFieldFilter.setFocused(true);
        }
        return true;
    }

    private boolean handleNavigateHistory(UserInput input, TextHistory.Direction direction) {
        if (this.textFieldFilter.isFocused()) {
            return this.textFieldFilter.getHistory(direction).map(newText -> {
                if (!input.isSimulate()) {
                    this.textFieldFilter.setValue((String)newText);
                }
                return true;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public void unfocus() {
        this.textFieldFilter.setFocused(false);
    }
}

