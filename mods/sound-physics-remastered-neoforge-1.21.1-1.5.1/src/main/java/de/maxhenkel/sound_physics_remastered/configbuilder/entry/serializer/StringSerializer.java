/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class StringSerializer
implements ValueSerializer<String> {
    public static final StringSerializer INSTANCE = new StringSerializer();

    @Override
    @Nullable
    public String deserialize(String str) {
        return str;
    }

    @Override
    @Nullable
    public String serialize(String val) {
        return val;
    }
}

