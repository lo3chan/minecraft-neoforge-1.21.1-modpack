/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.sun_moon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Mutable
    @Shadow
    @Final
    private static ResourceLocation SUN_LOCATION;
    @Mutable
    @Shadow
    @Final
    private static ResourceLocation MOON_LOCATION;

    @WrapOperation(method={"renderSky"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F")})
    public float[] redirectGetFogColorOverride(DimensionSpecialEffects instance, float skyAngle, float tickDelta, Operation<float[]> original) {
        if (SodiumExtraClientMod.options().detailSettings.sun) {
            return (float[])original.call(new Object[]{instance, Float.valueOf(skyAngle), Float.valueOf(tickDelta)});
        }
        return null;
    }

    @Inject(method={"allChanged()V"}, at={@At(value="TAIL")})
    private void postWorldRendererReload(CallbackInfo ci) {
        SUN_LOCATION = SodiumExtraClientMod.options().detailSettings.sun ? ResourceLocation.withDefaultNamespace((String)"textures/environment/sun.png") : ResourceLocation.fromNamespaceAndPath((String)"sodium-extra", (String)"textures/transparent.png");
        MOON_LOCATION = SodiumExtraClientMod.options().detailSettings.moon ? ResourceLocation.withDefaultNamespace((String)"textures/environment/moon_phases.png") : ResourceLocation.fromNamespaceAndPath((String)"sodium-extra", (String)"textures/transparent.png");
    }
}

