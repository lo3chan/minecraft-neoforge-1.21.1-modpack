/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import java.util.List;
import java.util.Optional;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class CombinedDragHandler
implements IDragHandler {
    private final List<IDragHandler> dragHandlers;

    public CombinedDragHandler(IDragHandler ... dragHandlers) {
        this.dragHandlers = List.of(dragHandlers);
    }

    @Override
    public Optional<IDragHandler> handleDragStart(Screen screen, UserInput input) {
        return this.dragHandlers.stream().flatMap(d -> d.handleDragStart(screen, input).stream()).findFirst();
    }

    @Override
    public boolean handleDragComplete(Screen screen, UserInput input) {
        for (IDragHandler handler : this.dragHandlers) {
            if (!handler.handleDragComplete(screen, input)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void handleDragCanceled() {
        for (IDragHandler handler : this.dragHandlers) {
            handler.handleDragCanceled();
        }
    }
}

