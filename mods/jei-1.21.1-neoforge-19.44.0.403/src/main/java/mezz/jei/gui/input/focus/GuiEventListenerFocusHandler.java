/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 */
package mezz.jei.gui.input.focus;

import mezz.jei.gui.input.focus.EditBoxFocusHandler;
import mezz.jei.gui.input.focus.IFocusHandler;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class GuiEventListenerFocusHandler
implements IFocusHandler {
    private final GuiEventListener guiEventListener;
    private boolean unfocused;

    public static IFocusHandler create(GuiEventListener guiEventListener) {
        if (guiEventListener instanceof EditBox) {
            EditBox editBox = (EditBox)guiEventListener;
            return new EditBoxFocusHandler(editBox);
        }
        return new GuiEventListenerFocusHandler(guiEventListener);
    }

    private GuiEventListenerFocusHandler(GuiEventListener guiEventListener) {
        this.guiEventListener = guiEventListener;
    }

    @Override
    public void unFocus() {
        this.unfocused = this.guiEventListener.isFocused();
        if (this.unfocused) {
            this.guiEventListener.setFocused(false);
        }
    }

    @Override
    public void focus() {
        if (this.unfocused && !this.guiEventListener.isFocused()) {
            this.guiEventListener.setFocused(true);
        }
        this.unfocused = false;
    }
}

