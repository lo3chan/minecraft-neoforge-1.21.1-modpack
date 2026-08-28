/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ZeroBitStorage
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.diebuddies.mixins.vines;

import net.diebuddies.physics.vines.FastBlockSearcher;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.util.ZeroBitStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ZeroBitStorage.class})
public class MixinZeroBitStorage
implements FastBlockSearcher {
    @Shadow
    @Final
    private int size;

    @Override
    public void getAllFast(FastBlockSearcherConsumer consumer) {
        consumer.accept(0, this.size);
    }
}

