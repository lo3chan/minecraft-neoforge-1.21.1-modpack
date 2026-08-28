/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nullable;

public class FloatSerializer
implements ValueSerializer<Float> {
    public static final FloatSerializer INSTANCE = new FloatSerializer();

    @Override
    @Nullable
    public Float deserialize(String str) {
        try {
            return Float.valueOf(Float.parseFloat(str));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public String serialize(Float val) {
        return String.valueOf(val);
    }
}

