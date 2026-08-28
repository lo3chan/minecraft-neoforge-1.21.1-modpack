/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.biome.Biome
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.flashyreese.mods.sodiumextra.mixin.sky_colors;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Biome.class})
public class MixinBiome {
    @Inject(method={"getSkyColor"}, at={@At(value="HEAD")}, cancellable=true)
    private void differentSkyColor(CallbackInfoReturnable<Integer> cir) {
        if (!SodiumExtraClientMod.options().detailSettings.skyColors) {
            cir.setReturnValue((Object)7907327);
        }
    }
}

