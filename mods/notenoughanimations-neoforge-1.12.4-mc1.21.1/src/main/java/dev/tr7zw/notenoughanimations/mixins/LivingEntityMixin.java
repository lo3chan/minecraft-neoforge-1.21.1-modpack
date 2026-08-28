/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public class LivingEntityMixin {
    @Inject(method={"tickHeadTurn(FF)F"}, at={@At(value="HEAD")}, cancellable=true)
    protected void tickHeadTurn(float f, float g, CallbackInfoReturnable<Float> info) {
        PlayerData data;
        if (this instanceof PlayerData && (data = (PlayerData)((Object)this)).isDisableBodyRotation()) {
            data.setDisableBodyRotation(false);
            info.setReturnValue((Object)Float.valueOf(g));
            info.cancel();
        }
    }
}

