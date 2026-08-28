/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.neoforged.neoforge.client.event.ScreenEvent$KeyPressed
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonPressed
 *  net.neoforged.neoforge.client.event.ScreenEvent$MouseButtonReleased
 */
package mezz.jei.neoforge.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.MouseUtil;
import mezz.jei.gui.input.UserInput;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class ForgeUserInput {
    private ForgeUserInput() {
    }

    public static UserInput fromEvent(ScreenEvent.KeyPressed keyEvent) {
        InputConstants.Key input = InputConstants.getKey((int)keyEvent.getKeyCode(), (int)keyEvent.getScanCode());
        double mouseX = MouseUtil.getX();
        double mouseY = MouseUtil.getY();
        int modifiers = keyEvent.getModifiers();
        return new UserInput(input, mouseX, mouseY, modifiers, InputType.IMMEDIATE);
    }

    public static Optional<UserInput> fromEvent(ScreenEvent.MouseButtonPressed event) {
        int button = event.getButton();
        if (button < 0) {
            return Optional.empty();
        }
        InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(button);
        UserInput userInput = new UserInput(input, event.getMouseX(), event.getMouseY(), 0, InputType.SIMULATE);
        return Optional.of(userInput);
    }

    public static Optional<UserInput> fromEvent(ScreenEvent.MouseButtonReleased event) {
        int button = event.getButton();
        if (button < 0) {
            return Optional.empty();
        }
        InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(button);
        UserInput userInput = new UserInput(input, event.getMouseX(), event.getMouseY(), 0, InputType.EXECUTE);
        return Optional.of(userInput);
    }
}

