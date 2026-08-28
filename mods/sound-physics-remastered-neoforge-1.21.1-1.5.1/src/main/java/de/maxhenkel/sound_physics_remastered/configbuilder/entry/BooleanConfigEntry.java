/*
 * Decompiled with CFR 0.152.
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry;

import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.AbstractConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;

public class BooleanConfigEntry
extends AbstractConfigEntry<Boolean> {
    public BooleanConfigEntry(CommentedPropertyConfig config, ValueSerializer<Boolean> serializer, String[] comments, String key, Boolean def) {
        super(config, serializer, comments, key, def);
        this.reload();
    }
}

