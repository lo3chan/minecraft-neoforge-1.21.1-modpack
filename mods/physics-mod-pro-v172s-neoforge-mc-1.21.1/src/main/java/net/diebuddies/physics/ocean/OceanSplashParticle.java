/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.util.Mth
 *  org.joml.Math
 */
package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class OceanSplashParticle
extends TextureSheetParticle {
    private float baseAlpha;

    public OceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
        float size = Math.random() * 0.9f + 0.3f;
        this.setSize(size, size);
        this.setLifetime(Math.randomInt(7) + 5);
        this.quadSize = size;
        this.gravity = 0.981f;
        this.oRoll = this.roll = Math.random() * (float)java.lang.Math.PI * 2.0f;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        float modifier = ConfigClient.oceanParticleAlpha * 0.5f;
        this.baseAlpha = Math.random() * modifier + modifier;
        this.setPos(x + this.xd, y + this.yd, z + this.zd);
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float renderPercent) {
        this.setAlpha((1.0f - org.joml.Math.min((float)1.0f, (float)(((float)this.age + renderPercent) / (float)this.lifetime))) * this.baseAlpha);
        super.render(vertexConsumer, camera, renderPercent);
    }

    public double getX(float renderPercent) {
        return Mth.lerp((double)renderPercent, (double)this.xo, (double)this.x);
    }

    public double getY(float renderPercent) {
        return Mth.lerp((double)renderPercent, (double)this.yo, (double)this.y);
    }

    public double getZ(float renderPercent) {
        return Mth.lerp((double)renderPercent, (double)this.zo, (double)this.z);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
            OceanSplashParticle particle = new OceanSplashParticle(clientLevel, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}

