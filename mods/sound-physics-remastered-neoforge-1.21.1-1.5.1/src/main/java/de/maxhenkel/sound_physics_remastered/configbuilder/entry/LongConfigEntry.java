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

public class LongConfigEntry
extends AbstractRangedConfigEntry<Long> {
    public LongConfigEntry(CommentedPropertyConfig config, ValueSerializer<Long> serializer, String[] comments, String key, Long def, @Nullable Long min, @Nullable Long max) {
        super(config, serializer, comments, key, def, min, max);
        this.reload();
    }

    @Override
    @Nonnull
    Long minimumPossibleValue() {
        return Long.MIN_VALUE;
    }

    @Override
    @Nonnull
    Long maximumPossibleValue() {
        return Long.MAX_VALUE;
    }

    @Override
    Long fixValue(Long value) {
        return Math.max(Math.min(value, (Long)this.max), (Long)this.min);
    }
}

