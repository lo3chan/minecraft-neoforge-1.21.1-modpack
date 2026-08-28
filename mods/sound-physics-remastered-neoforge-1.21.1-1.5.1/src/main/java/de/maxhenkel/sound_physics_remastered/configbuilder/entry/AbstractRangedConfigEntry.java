/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry;

import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.AbstractConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.RangedConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractRangedConfigEntry<T>
extends AbstractConfigEntry<T>
implements RangedConfigEntry<T> {
    @Nonnull
    protected final T min;
    @Nonnull
    protected final T max;

    public AbstractRangedConfigEntry(CommentedPropertyConfig config, ValueSerializer<T> serializer, String[] comments, String key, T def, @Nullable T min, @Nullable T max) {
        super(config, serializer, comments, key, def);
        this.min = min != null ? min : this.minimumPossibleValue();
        this.max = max != null ? max : this.maximumPossibleValue();
    }

    @Override
    @Nonnull
    public T getMin() {
        return this.min;
    }

    @Override
    @Nonnull
    public T getMax() {
        return this.max;
    }

    @Nonnull
    abstract T minimumPossibleValue();

    @Nonnull
    abstract T maximumPossibleValue();
}

