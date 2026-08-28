/*
 * Decompiled with CFR 0.152.
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.custom.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import java.util.UUID;

public class UUIDSerializer
implements ValueSerializer<UUID> {
    public static final UUIDSerializer INSTANCE = new UUIDSerializer();

    @Override
    public UUID deserialize(String str) {
        return UUID.fromString(str);
    }

    @Override
    public String serialize(UUID val) {
        return val.toString();
    }
}

