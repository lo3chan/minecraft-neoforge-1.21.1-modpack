/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseFireBlock
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.diebuddies.mixins.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BaseFireBlock.class})
public class MixinBaseFireBlock {
    @Redirect(method={"animateTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    public void addParticle(Level level, ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
        if (!SmokeHelper.addParticle(level, x, y, z, ConfigClient.smokeFire)) {
            level.addParticle(options, x, y, z, vx, vy, vz);
        }
    }
}

