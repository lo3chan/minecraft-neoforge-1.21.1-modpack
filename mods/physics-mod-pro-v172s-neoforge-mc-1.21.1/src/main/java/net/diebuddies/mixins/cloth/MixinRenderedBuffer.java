/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder$Result
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.diebuddies.physics.verlet.RenderedBufferAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ByteBufferBuilder.Result.class})
public class MixinRenderedBuffer
implements RenderedBufferAccessor {
    @Unique
    private boolean ignoreRelease;

    @Inject(at={@At(value="HEAD")}, method={"close"}, cancellable=true)
    public void release(CallbackInfo info) {
        if (this.ignoreRelease) {
            info.cancel();
        }
    }

    @Override
    public void setIgnoreRelease(boolean ignoreRelease) {
        this.ignoreRelease = ignoreRelease;
    }
}

