/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.List;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public interface OptionRow
extends Renderable,
GuiEventListener,
NarratableEntry {
    public Option getOption();

    public LayoutBounds getDimensions();

    public List<NarratableEntry> collectNarratables();

    public void releaseActionButtonLayoutHold();

    public boolean handleBackNavigation();

    public boolean undoFocusedActionButton();

    public void clearActionButtonFocus();
}

