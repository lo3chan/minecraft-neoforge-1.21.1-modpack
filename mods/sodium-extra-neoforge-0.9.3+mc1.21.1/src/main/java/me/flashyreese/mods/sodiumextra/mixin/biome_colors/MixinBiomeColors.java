/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BiomeColors
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.flashyreese.mods.sodiumextra.mixin.biome_colors;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.BiomeColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BiomeColors.class})
public class MixinBiomeColors {
    @Inject(method={"getAverageGrassColor"}, at={@At(value="RETURN")}, cancellable=true)
    private static void grassColor(CallbackInfoReturnable<Integer> cir) {
        if (!SodiumExtraClientMod.options().detailSettings.biomeColors) {
            cir.setReturnValue((Object)9551193);
        }
    }

    @Inject(method={"getAverageWaterColor"}, at={@At(value="RETURN")}, cancellable=true)
    private static void waterColor(CallbackInfoReturnable<Integer> cir) {
        if (!SodiumExtraClientMod.options().detailSettings.biomeColors) {
            cir.setReturnValue((Object)4159204);
        }
    }

    @Inject(method={"getAverageFoliageColor"}, at={@At(value="RETURN")}, cancellable=true)
    private static void foliageColor(CallbackInfoReturnable<Integer> cir) {
        if (!SodiumExtraClientMod.options().detailSettings.biomeColors) {
            cir.setReturnValue((Object)5877296);
        }
    }
}

