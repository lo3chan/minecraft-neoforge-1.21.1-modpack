/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public interface ValueSerializer<T> {
    @Nullable
    public T deserialize(String var1);

    @Nullable
    public String serialize(T var1);
}

