/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input;

import java.util.Optional;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public interface IDragHandler {
    public Optional<IDragHandler> handleDragStart(Screen var1, UserInput var2);

    public boolean handleDragComplete(Screen var1, UserInput var2);

    default public void handleDragCanceled() {
    }
}

