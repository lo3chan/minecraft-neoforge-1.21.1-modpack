/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.optifine.shaders.Program
 *  net.optifine.shaders.Shaders
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.optifine;

import net.diebuddies.compat.Optifine;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"net.optifine.shaders.Programs"})
public class MixinPrograms {
    @Shadow(remap=false)
    private Program makeGbuffers(String name, Program backupProgram) {
        return null;
    }

    @Shadow(remap=false)
    private Program makeShadow(String name, Program backupProgram) {
        return null;
    }

    @Inject(at={@At(value="HEAD")}, method={"getPrograms"}, remap=false)
    private void physicsmod$addOceanShader(CallbackInfoReturnable<Program[]> info) {
        Optifine.oceanProgram = this.makeGbuffers("gbuffers_water", Shaders.ProgramTerrain);
    }

    @Inject(at={@At(value="HEAD")}, method={"makeShadowcomps"}, remap=false)
    private void physicsmod$addOceanShadowShader(String prefix, int count, CallbackInfoReturnable<Program[]> info) {
        Optifine.oceanShadowProgram = this.makeShadow("shadow", Shaders.ProgramNone);
    }
}

