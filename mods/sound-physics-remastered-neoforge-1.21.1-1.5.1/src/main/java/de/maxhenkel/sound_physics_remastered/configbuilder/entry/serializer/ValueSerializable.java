/*
 * Decompiled with CFR 0.152.
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer;

import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface ValueSerializable {
    public Class<? extends ValueSerializer<?>> value();
}

