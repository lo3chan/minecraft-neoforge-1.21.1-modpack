/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public interface IUserInputHandler {
    public Optional<IUserInputHandler> handleUserInput(Screen var1, UserInput var2, IInternalKeyMappings var3);

    default public void unfocus() {
    }

    default public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return Optional.empty();
    }

    default public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        return Optional.empty();
    }
}

