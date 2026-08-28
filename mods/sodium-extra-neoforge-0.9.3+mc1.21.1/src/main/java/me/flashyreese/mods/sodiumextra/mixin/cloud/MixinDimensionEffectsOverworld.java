/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$OverworldEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$SkyType
 *  org.spongepowered.asm.mixin.Mixin
 */
package me.flashyreese.mods.sodiumextra.mixin.cloud;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={DimensionSpecialEffects.OverworldEffects.class})
public abstract class MixinDimensionEffectsOverworld
extends DimensionSpecialEffects {
    public MixinDimensionEffectsOverworld(float cloudsHeight, boolean alternateSkyColor, DimensionSpecialEffects.SkyType skyType, boolean brightenLighting, boolean darkened) {
        super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened);
    }

    public float getCloudHeight() {
        return SodiumExtraClientMod.options().extraSettings.cloudHeightOverride ? (float)SodiumExtraClientMod.options().extraSettings.cloudHeight : super.getCloudHeight();
    }
}

