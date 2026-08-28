/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry;

import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.AbstractRangedConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DoubleConfigEntry
extends AbstractRangedConfigEntry<Double> {
    public DoubleConfigEntry(CommentedPropertyConfig config, ValueSerializer<Double> serializer, String[] comments, String key, Double def, @Nullable Double min, @Nullable Double max) {
        super(config, serializer, comments, key, def, min, max);
        this.reload();
    }

    @Override
    @Nonnull
    Double minimumPossibleValue() {
        return Double.MIN_VALUE;
    }

    @Override
    @Nonnull
    Double maximumPossibleValue() {
        return Double.MAX_VALUE;
    }

    @Override
    Double fixValue(Double value) {
        return Math.max(Math.min(value, (Double)this.max), (Double)this.min);
    }
}

