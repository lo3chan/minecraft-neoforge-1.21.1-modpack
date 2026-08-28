/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package net.irisshaders.iris.mixin.texture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.irisshaders.iris.pbr.TextureTracker;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={AbstractTexture.class})
public class MixinAbstractTexture {
    @Shadow
    protected int id;

    @WrapOperation(method={"getId()I"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/TextureUtil;generateTextureId()I", remap=false)})
    private int iris$afterGenerateId(Operation<Integer> original) {
        int id = (Integer)original.call(new Object[0]);
        TextureTracker.INSTANCE.trackTexture(id, (AbstractTexture)this);
        return id;
    }
}

