/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.Options
 *  net.minecraft.client.Options$FieldAccess
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package net.irisshaders.iris.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={Options.class})
public abstract class MixinMaxFpsCrashFix {
    @Unique
    private void iris$resetFramerateLimit(Options.FieldAccess instance, String name, OptionInstance<Integer> option) {
        if ((Integer)option.get() == 0) {
            option.set((Object)120);
        }
        instance.process(name, option);
    }
}

