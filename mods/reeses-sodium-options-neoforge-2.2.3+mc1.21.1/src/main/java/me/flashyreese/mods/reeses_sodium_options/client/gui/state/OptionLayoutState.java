/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.state;

import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import org.jetbrains.annotations.Nullable;

public final class OptionLayoutState {
    @Nullable
    private LayoutBounds bounds;
    @Nullable
    private LayoutBounds parentBounds;

    @Nullable
    public LayoutBounds bounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable LayoutBounds bounds) {
        this.bounds = bounds;
    }

    @Nullable
    public LayoutBounds parentBounds() {
        return this.parentBounds;
    }

    public void setParentBounds(@Nullable LayoutBounds parentBounds) {
        this.parentBounds = parentBounds;
    }
}

