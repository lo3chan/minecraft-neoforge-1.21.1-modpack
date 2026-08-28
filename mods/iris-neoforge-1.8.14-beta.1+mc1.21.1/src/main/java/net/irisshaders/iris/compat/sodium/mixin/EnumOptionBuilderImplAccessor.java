/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.compat.sodium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets={"net.caffeinemc.mods.sodium.client.config.builder.EnumOptionBuilderImpl"})
public interface EnumOptionBuilderImplAccessor<E extends Enum<E>> {
    @Accessor(value="enumClass")
    public Class<E> getEnumClass();
}

