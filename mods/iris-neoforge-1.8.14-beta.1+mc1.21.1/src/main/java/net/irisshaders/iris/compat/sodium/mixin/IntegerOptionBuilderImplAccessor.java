/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.irisshaders.iris.compat.sodium.mixin;

import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets={"net.caffeinemc.mods.sodium.client.config.builder.IntegerOptionBuilderImpl"})
public interface IntegerOptionBuilderImplAccessor {
    @Accessor(value="valueFormatter")
    public ControlValueFormatter iris$getValueFormatter();
}

