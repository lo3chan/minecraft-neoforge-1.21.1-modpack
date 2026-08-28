/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class IntegerSerializer
implements ValueSerializer<Integer> {
    public static final IntegerSerializer INSTANCE = new IntegerSerializer();

    @Override
    @Nullable
    public Integer deserialize(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Integer val) {
        return String.valueOf(val);
    }
}

