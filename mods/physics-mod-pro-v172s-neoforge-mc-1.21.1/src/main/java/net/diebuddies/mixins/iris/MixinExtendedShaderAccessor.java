/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Uniform
 *  net.irisshaders.iris.pipeline.programs.ExtendedShader
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.shaders.Uniform;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value={ExtendedShader.class}, remap=false)
public interface MixinExtendedShaderAccessor {
    @Accessor(value="modelViewInverse")
    public Uniform getModelViewInverse();

    @Accessor(value="normalMatrix")
    public Uniform getNormalMatrix();
}

