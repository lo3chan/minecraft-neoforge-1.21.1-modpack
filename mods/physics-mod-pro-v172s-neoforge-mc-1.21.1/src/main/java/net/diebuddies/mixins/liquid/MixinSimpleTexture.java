/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.liquid;

import com.mojang.blaze3d.platform.NativeImage;
import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.minecraft.client.renderer.texture.SimpleTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SimpleTexture.class})
public class MixinSimpleTexture
implements SimpleTextureDimension {
    @Unique
    private int tWidthP;
    @Unique
    private int tHeightP;

    @Inject(at={@At(value="HEAD")}, method={"doLoad"})
    private void doLoad(NativeImage nativeImage, boolean bl, boolean bl2, CallbackInfo info) {
        this.tWidthP = nativeImage.getWidth();
        this.tHeightP = nativeImage.getHeight();
    }

    @Override
    public int getWidth() {
        return this.tWidthP;
    }

    @Override
    public int getHeight() {
        return this.tHeightP;
    }
}

