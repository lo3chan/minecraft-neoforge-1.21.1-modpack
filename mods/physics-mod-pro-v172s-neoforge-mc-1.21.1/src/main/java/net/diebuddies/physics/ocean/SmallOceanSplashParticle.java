/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.particles.SimpleParticleType
 */
package net.diebuddies.physics.ocean;

import net.diebuddies.math.Math;
import net.diebuddies.physics.ocean.OceanSplashParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SmallOceanSplashParticle
extends OceanSplashParticle {
    public SmallOceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        super(clientLevel, x, y, z, vx, vy, vz);
        float size = Math.random() * 0.6f + 0.15f;
        this.setSize(size, size);
        this.quadSize = size;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
            SmallOceanSplashParticle particle = new SmallOceanSplashParticle(clientLevel, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}

