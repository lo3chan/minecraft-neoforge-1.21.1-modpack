/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.pathways.HandRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.diebuddies.mixins.iris;

import net.irisshaders.iris.pathways.HandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value={HandRenderer.class}, remap=false)
public interface MixinHandRendererAccessor {
    @Accessor(value="renderingSolid")
    public void setRenderingSolid(boolean var1);
}

