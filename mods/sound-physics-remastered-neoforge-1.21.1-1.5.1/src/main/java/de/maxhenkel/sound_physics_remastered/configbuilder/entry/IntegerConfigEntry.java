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

public class IntegerConfigEntry
extends AbstractRangedConfigEntry<Integer> {
    public IntegerConfigEntry(CommentedPropertyConfig config, ValueSerializer<Integer> serializer, String[] comments, String key, Integer def, @Nullable Integer min, @Nullable Integer max) {
        super(config, serializer, comments, key, def, min, max);
        this.reload();
    }

    @Override
    @Nonnull
    Integer minimumPossibleValue() {
        return Integer.MIN_VALUE;
    }

    @Override
    @Nonnull
    Integer maximumPossibleValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    Integer fixValue(Integer value) {
        return Math.max(Math.min(value, (Integer)this.max), (Integer)this.min);
    }
}

