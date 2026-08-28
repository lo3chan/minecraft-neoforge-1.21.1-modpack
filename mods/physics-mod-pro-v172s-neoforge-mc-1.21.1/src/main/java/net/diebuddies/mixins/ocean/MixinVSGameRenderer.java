/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GameRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package net.diebuddies.mixins.ocean;

import net.diebuddies.physics.StarterClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={GameRenderer.class})
public class MixinVSGameRenderer {
    @ModifyVariable(method={"render"}, at=@At(value="HEAD"), ordinal=0)
    private float physicsmod$fixJitter(float tick) {
        if (StarterClient.valkyrienSkies) {
            return Math.min(0.999989f, tick);
        }
        return tick;
    }
}

