/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.smoke;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Shadow
    private ClientLevel level;
    @Unique
    private Set<ParticleType<?>> smokeSpawners;

    @Inject(at={@At(value="HEAD")}, method={"addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V"}, cancellable=true)
    public void addParticle(ParticleOptions particleOptions, boolean bl, boolean bl2, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
        this.spawnPhysicsSmoke(particleOptions, x, y, z, vx, vy, vz, info);
    }

    @Unique
    private void spawnPhysicsSmoke(ParticleOptions particleOptions, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
        boolean spawnSmoke = ConfigClient.smokePhysics;
        if (!spawnSmoke || !this.getSmokeSpawners().contains(particleOptions.getType()) || this.level == null) {
            return;
        }
        if (SmokeHelper.addParticle((Level)this.level, x, y, z, ConfigClient.smokeOther)) {
            info.cancel();
        }
    }

    @Unique
    private Set<ParticleType<?>> getSmokeSpawners() {
        if (this.smokeSpawners == null) {
            this.smokeSpawners = new ObjectOpenHashSet();
            this.smokeSpawners.add((ParticleType<?>)ParticleTypes.LARGE_SMOKE);
            this.smokeSpawners.add((ParticleType<?>)ParticleTypes.CAMPFIRE_COSY_SMOKE);
            this.smokeSpawners.add((ParticleType<?>)ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
        }
        return this.smokeSpawners;
    }
}

