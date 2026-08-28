/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package me.flashyreese.mods.sodiumextra.mixin.stars;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @WrapOperation(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F")})
    public float redirectGetStarBrightness(ClientLevel instance, float f, Operation<Float> original) {
        if (SodiumExtraClientMod.options().detailSettings.stars) {
            return ((Float)original.call(new Object[]{instance, Float.valueOf(f)})).floatValue();
        }
        return 0.0f;
    }
}

