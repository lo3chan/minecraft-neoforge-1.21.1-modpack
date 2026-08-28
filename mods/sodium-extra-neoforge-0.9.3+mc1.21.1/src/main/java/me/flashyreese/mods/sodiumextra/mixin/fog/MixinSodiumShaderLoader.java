/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package me.flashyreese.mods.sodiumextra.mixin.fog;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.flashyreese.mods.sodiumextra.client.fog.FogShaderTransformer;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ShaderLoader.class}, remap=false)
public class MixinSodiumShaderLoader {
    @ModifyReturnValue(method={"getShaderSource"}, at={@At(value="RETURN")})
    private static String sodiumExtra$injectSodiumShaderSource(String source, ResourceLocation location) {
        return FogShaderTransformer.injectSodiumShaderSource(source, location);
    }
}

