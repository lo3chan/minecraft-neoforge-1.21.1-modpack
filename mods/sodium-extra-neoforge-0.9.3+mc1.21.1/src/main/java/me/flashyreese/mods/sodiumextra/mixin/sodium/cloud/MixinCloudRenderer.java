/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.immediate.CloudRenderer
 *  net.minecraft.client.Options
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package me.flashyreese.mods.sodiumextra.mixin.sodium.cloud;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.caffeinemc.mods.sodium.client.render.immediate.CloudRenderer;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={CloudRenderer.class})
public class MixinCloudRenderer {
    @Redirect(method={"getCloudRenderDistance"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/Options;getEffectiveRenderDistance()I"))
    private static int modifyCloudRenderDistance(Options options) {
        return options.getEffectiveRenderDistance() * SodiumExtraClientMod.options().extraSettings.cloudDistance / 100;
    }
}

