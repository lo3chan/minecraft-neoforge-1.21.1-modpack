/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Math
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package net.diebuddies.minecraft.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.WeatherParticle;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RainParticle
extends WeatherParticle {
    private static Quaternionf movementRotation = new Quaternionf();
    private Vector3f v0;
    private Vector3f v1;
    private Vector3f v2;
    private Vector3f v3;

    public RainParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        super(clientLevel, x, y, z, vx, vy, vz);
        this.quadSize = 0.23f;
        this.setColor(255, 255, 255, (int)((float)(175 + (int)((double)Math.random() * 40.0)) * ConfigClient.particleRainOpacity));
        double lengthSquared = Vector3d.lengthSquared((double)vx, (double)vy, (double)vz);
        if (lengthSquared != 0.0) {
            double invLength = 1.0 / org.joml.Math.sqrt((double)lengthSquared);
            vx *= invLength;
            vy *= invLength;
            vz *= invLength;
        } else {
            vy = -1.0;
        }
        this.dampingX = 1.0;
        this.dampingY = 1.0;
        this.dampingZ = 1.0;
        this.gravity = 0.0f;
        this.rotationTo(movementRotation, (float)vx, (float)vy, (float)vz).rotateX((float)((double)Math.random() * 10.0));
        this.v0 = new Vector3f();
        this.transformUnit(movementRotation, -this.quadSize, -this.quadSize, this.v0);
        this.v1 = new Vector3f();
        this.transformUnit(movementRotation, -this.quadSize, this.quadSize, this.v1);
        this.v2 = new Vector3f();
        this.transformUnit(movementRotation, this.quadSize, this.quadSize, this.v2);
        this.v3 = new Vector3f();
        this.transformUnit(movementRotation, this.quadSize, -this.quadSize, this.v3);
    }

    public void transformUnit(Quaternionf src, float x, float y, Vector3f dest) {
        float xx = src.x * src.x;
        float xy = src.x * src.y;
        float xz = src.x * src.z;
        float xw = src.x * src.w;
        float yy = src.y * src.y;
        float yz = src.y * src.z;
        float yw = src.y * src.w;
        float zz = src.z * src.z;
        float zw = src.z * src.w;
        dest.set(org.joml.Math.fma((float)org.joml.Math.fma((float)-2.0f, (float)(yy + zz), (float)1.0f), (float)x, (float)(2.0f * (xy - zw) * y)), org.joml.Math.fma((float)(2.0f * (xy + zw)), (float)x, (float)(org.joml.Math.fma((float)-2.0f, (float)(xx + zz), (float)1.0f) * y)), org.joml.Math.fma((float)(2.0f * (xz - yw)), (float)x, (float)(2.0f * (yz + xw) * y)));
    }

    public Quaternionf rotationTo(Quaternionf src, float tx, float ty, float tz) {
        float dot = -tx;
        if (dot < -0.999999f) {
            src.x = 0.0f;
            src.y = 1.0f;
            src.z = 0.0f;
            src.w = 0.0f;
        } else {
            float sd2 = org.joml.Math.sqrt((float)((1.0f + dot) * 2.0f));
            float isd2 = 1.0f / sd2;
            float cx = 0.0f;
            float cy = tz;
            float cz = -ty;
            float x = cx * isd2;
            float y = cy * isd2;
            float z = cz * isd2;
            float w = sd2 * 0.5f;
            float n2 = org.joml.Math.invsqrt((float)org.joml.Math.fma((float)x, (float)x, (float)org.joml.Math.fma((float)y, (float)y, (float)org.joml.Math.fma((float)z, (float)z, (float)(w * w)))));
            src.x = x * n2;
            src.y = y * n2;
            src.z = z * n2;
            src.w = w * n2;
        }
        return src;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float renderPercent) {
        RenderSystem.disableCull();
        Vec3 cameraPos = camera.getPosition();
        double px = org.joml.Math.lerp((double)this.xo, (double)this.x, (double)renderPercent);
        double py = org.joml.Math.lerp((double)this.yo, (double)this.y, (double)renderPercent);
        double pz = org.joml.Math.lerp((double)this.zo, (double)this.z, (double)renderPercent);
        float currentX = (float)(px - cameraPos.x());
        float currentY = (float)(py - cameraPos.y());
        float currentZ = (float)(pz - cameraPos.z());
        float s0 = this.getU0();
        float s1 = this.getU1();
        float t0 = this.getV0();
        float t1 = this.getV1();
        int light = this.getLightColor(renderPercent);
        if (StarterClient.sodium) {
            Sodium.renderParticle(vertexConsumer, this.v0, this.v1, this.v2, this.v3, currentX, currentY, currentZ, s0, t0, s1, t1, this.argb, light);
        } else {
            vertexConsumer.addVertex(this.v0.x + currentX, this.v0.y + currentY, this.v0.z + currentZ).setUv(s1, t1).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(this.v1.x + currentX, this.v1.y + currentY, this.v1.z + currentZ).setUv(s1, t0).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(this.v2.x + currentX, this.v2.y + currentY, this.v2.z + currentZ).setUv(s0, t0).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(this.v3.x + currentX, this.v3.y + currentY, this.v3.z + currentZ).setUv(s0, t1).setColor(this.r, this.g, this.b, this.a).setLight(light);
        }
    }

    @Override
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
            RainParticle particle = new RainParticle(clientLevel, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}

