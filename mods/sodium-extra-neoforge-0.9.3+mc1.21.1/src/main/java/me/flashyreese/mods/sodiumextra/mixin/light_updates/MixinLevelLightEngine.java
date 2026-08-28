/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.flashyreese.mods.sodiumextra.mixin.light_updates;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LevelLightEngine.class})
public class MixinLevelLightEngine {
    @Inject(at={@At(value="HEAD")}, method={"checkBlock"}, cancellable=true)
    public void checkBlock(BlockPos pos, CallbackInfo ci) {
        if (!SodiumExtraClientMod.options().renderSettings.lightUpdates) {
            ci.cancel();
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"runLightUpdates"}, cancellable=true)
    public void doLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if (!SodiumExtraClientMod.options().renderSettings.lightUpdates) {
            cir.setReturnValue((Object)0);
        }
    }
}

