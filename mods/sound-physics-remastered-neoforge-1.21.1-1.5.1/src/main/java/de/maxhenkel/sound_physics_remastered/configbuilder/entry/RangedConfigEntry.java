/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.ConfigEntry;
import javax.annotation.Nonnull;

public interface RangedConfigEntry<T>
extends ConfigEntry<T> {
    @Nonnull
    public T getMin();

    @Nonnull
    public T getMax();
}

