/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IMouseOverable;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class SameElementInputHandler
implements IUserInputHandler {
    private final IUserInputHandler handler;
    private final IMouseOverable mouseOverable;

    public SameElementInputHandler(IUserInputHandler handler, IMouseOverable mouseOverable) {
        this.handler = handler;
        this.mouseOverable = mouseOverable;
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        double mouseY;
        double mouseX = input.getMouseX();
        if (this.mouseOverable.isMouseOver(mouseX, mouseY = input.getMouseY())) {
            return this.handler.handleUserInput(screen, input, keyBindings).map(handled -> this);
        }
        return Optional.empty();
    }

    @Override
    public void unfocus() {
        this.handler.unfocus();
    }

    @Override
    public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (this.mouseOverable.isMouseOver(mouseX, mouseY)) {
            return this.handler.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
        }
        return Optional.empty();
    }

    @Override
    public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        if (this.mouseOverable.isMouseOver(mouseX, mouseY)) {
            return this.handler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY).map(handled -> this);
        }
        return Optional.empty();
    }
}

