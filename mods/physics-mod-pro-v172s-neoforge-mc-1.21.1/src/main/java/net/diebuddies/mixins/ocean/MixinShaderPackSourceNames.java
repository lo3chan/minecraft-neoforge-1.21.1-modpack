/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.ocean;

import com.google.common.collect.ImmutableList;
import net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value={ShaderPackSourceNames.class})
public class MixinShaderPackSourceNames {
    @Unique
    private static boolean physicsmod$hasAdded = false;

    @Inject(at={@At(value="TAIL")}, method={"addStarts"}, remap=false)
    private static void physicsmod$addOceanFilesSupport(ImmutableList.Builder<String> potentialFileNames, String baseName, CallbackInfo info) {
        if (!physicsmod$hasAdded) {
            physicsmod$hasAdded = !physicsmod$hasAdded;
            potentialFileNames.add((Object)"physics_ocean.vsh");
            potentialFileNames.add((Object)"physics_ocean.tcs");
            potentialFileNames.add((Object)"physics_ocean.tes");
            potentialFileNames.add((Object)"physics_ocean.gsh");
            potentialFileNames.add((Object)"physics_ocean.fsh");
            potentialFileNames.add((Object)"physics_ocean_shadow.vsh");
            potentialFileNames.add((Object)"physics_ocean_shadow.tcs");
            potentialFileNames.add((Object)"physics_ocean_shadow.tes");
            potentialFileNames.add((Object)"physics_ocean_shadow.gsh");
            potentialFileNames.add((Object)"physics_ocean_shadow.fsh");
            potentialFileNames.add((Object)"physics_liquid.vsh");
            potentialFileNames.add((Object)"physics_liquid.tcs");
            potentialFileNames.add((Object)"physics_liquid.tes");
            potentialFileNames.add((Object)"physics_liquid.gsh");
            potentialFileNames.add((Object)"physics_liquid.fsh");
            potentialFileNames.add((Object)"physics_liquid_shadow.vsh");
            potentialFileNames.add((Object)"physics_liquid_shadow.tcs");
            potentialFileNames.add((Object)"physics_liquid_shadow.tes");
            potentialFileNames.add((Object)"physics_liquid_shadow.gsh");
            potentialFileNames.add((Object)"physics_liquid_shadow.fsh");
        }
    }
}

