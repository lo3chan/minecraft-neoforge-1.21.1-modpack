/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import java.util.function.Supplier;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class ProxyInputHandler
implements IUserInputHandler {
    private final Supplier<IUserInputHandler> source;

    public ProxyInputHandler(Supplier<IUserInputHandler> source) {
        this.source = source;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("source", (Object)this.source.get()).toString();
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        return this.source.get().handleUserInput(screen, input, keyBindings);
    }

    @Override
    public void unfocus() {
        this.source.get().unfocus();
    }

    @Override
    public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.source.get().handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
    }

    @Override
    public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        return this.source.get().handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY);
    }
}

