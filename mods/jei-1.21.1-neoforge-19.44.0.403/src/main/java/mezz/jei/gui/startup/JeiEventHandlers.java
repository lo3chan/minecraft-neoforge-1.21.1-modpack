/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.startup;

import mezz.jei.gui.events.GuiEventHandler;
import mezz.jei.gui.input.ClientInputHandler;
import mezz.jei.gui.startup.ResourceReloadHandler;

public record JeiEventHandlers(GuiEventHandler guiEventHandler, ClientInputHandler clientInputHandler, ResourceReloadHandler resourceReloadHandler) {
}

