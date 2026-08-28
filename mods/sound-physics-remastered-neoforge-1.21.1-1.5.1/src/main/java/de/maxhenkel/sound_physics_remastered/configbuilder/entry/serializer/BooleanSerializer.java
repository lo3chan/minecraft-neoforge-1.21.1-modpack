/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class BooleanSerializer
implements ValueSerializer<Boolean> {
    public static final BooleanSerializer INSTANCE = new BooleanSerializer();

    @Override
    @Nullable
    public Boolean deserialize(String str) {
        return Boolean.valueOf(str);
    }

    @Override
    @Nullable
    public String serialize(Boolean val) {
        return String.valueOf(val);
    }
}

