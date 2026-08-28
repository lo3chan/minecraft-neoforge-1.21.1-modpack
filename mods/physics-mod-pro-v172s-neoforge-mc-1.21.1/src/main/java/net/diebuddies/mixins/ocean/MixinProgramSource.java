/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.shaderpack.programs.ProgramSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.diebuddies.mixins.ocean;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ProgramSource.class}, remap=false)
public interface MixinProgramSource {
    @Accessor
    @Mutable
    public void setVertexSource(String var1);

    @Accessor
    @Mutable
    public void setFragmentSource(String var1);
}

