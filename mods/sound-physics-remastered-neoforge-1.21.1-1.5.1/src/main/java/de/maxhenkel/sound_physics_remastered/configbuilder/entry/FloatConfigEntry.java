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

public class FloatConfigEntry
extends AbstractRangedConfigEntry<Float> {
    public FloatConfigEntry(CommentedPropertyConfig config, ValueSerializer<Float> serializer, String[] comments, String key, Float def, @Nullable Float min, @Nullable Float max) {
        super(config, serializer, comments, key, def, min, max);
        this.reload();
    }

    @Override
    @Nonnull
    Float minimumPossibleValue() {
        return Float.valueOf(Float.MIN_VALUE);
    }

    @Override
    @Nonnull
    Float maximumPossibleValue() {
        return Float.valueOf(Float.MAX_VALUE);
    }

    @Override
    Float fixValue(Float value) {
        return Float.valueOf(Math.max(Math.min(value.floatValue(), ((Float)this.max).floatValue()), ((Float)this.min).floatValue()));
    }
}

