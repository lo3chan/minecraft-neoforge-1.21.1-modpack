/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.prevent_shaders;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @Inject(method={"togglePostEffect"}, at={@At(value="HEAD")}, cancellable=true)
    private void preventShaders(CallbackInfo ci) {
        if (SodiumExtraClientMod.options().extraSettings.preventShaders) {
            ci.cancel();
        }
    }

    @Inject(method={"loadEffect"}, at={@At(value="HEAD")}, cancellable=true)
    private void dontLoadShader(ResourceLocation identifier, CallbackInfo ci) {
        if (SodiumExtraClientMod.options().extraSettings.preventShaders) {
            ci.cancel();
        }
    }
}

