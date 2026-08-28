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
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
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
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SnowParticle
extends WeatherParticle {
    private static Vector3f position0 = new Vector3f(-1.0f, -1.0f, 0.0f);
    private static Vector3f position1 = new Vector3f(-1.0f, 1.0f, 0.0f);
    private static Vector3f position2 = new Vector3f(1.0f, 1.0f, 0.0f);
    private static Vector3f position3 = new Vector3f(1.0f, -1.0f, 0.0f);
    private static Vector3f tmp0 = new Vector3f();
    private static Vector3f tmp1 = new Vector3f();
    private static Vector3f tmp2 = new Vector3f();
    private static Vector3f tmp3 = new Vector3f();
    private static float oldRotX;
    private static float oldRotY;
    private static float oldRotZ;
    private static float oldRotW;

    public SnowParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        super(clientLevel, x, y, z, vx, vy, vz);
        this.gravity = 0.002f;
        this.dampingX = 0.999;
        this.dampingY = 0.98;
        this.dampingZ = 0.999;
        this.setColor(255, 255, 255, (int)((float)(155 + (int)((double)Math.random() * 40.0)) * ConfigClient.particleSnowOpacity));
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
        Quaternionf cameraRotation = camera.rotation();
        if (oldRotX != cameraRotation.x() || oldRotY != cameraRotation.y() || oldRotZ != cameraRotation.z() || oldRotW != cameraRotation.w()) {
            float quadSize = this.getQuadSize();
            this.tmpRotation.set(cameraRotation.x(), cameraRotation.y(), cameraRotation.z(), cameraRotation.w());
            this.tmpRotation.transform(tmp0.set((Vector3fc)position0)).mul(quadSize);
            this.tmpRotation.transform(tmp1.set((Vector3fc)position1)).mul(quadSize);
            this.tmpRotation.transform(tmp2.set((Vector3fc)position2)).mul(quadSize);
            this.tmpRotation.transform(tmp3.set((Vector3fc)position3)).mul(quadSize);
            oldRotX = cameraRotation.x();
            oldRotY = cameraRotation.y();
            oldRotZ = cameraRotation.z();
            oldRotW = cameraRotation.w();
        }
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(renderPercent);
        if (StarterClient.sodium) {
            Sodium.renderParticle(vertexConsumer, tmp0, tmp1, tmp2, tmp3, currentX, currentY, currentZ, u0, v0, u1, v1, this.argb, light);
        } else {
            vertexConsumer.addVertex(tmp0.x() + currentX, tmp0.y() + currentY, tmp0.z() + currentZ).setUv(u1, v1).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(tmp1.x() + currentX, tmp1.y() + currentY, tmp1.z() + currentZ).setUv(u1, v0).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(tmp2.x() + currentX, tmp2.y() + currentY, tmp2.z() + currentZ).setUv(u0, v0).setColor(this.r, this.g, this.b, this.a).setLight(light);
            vertexConsumer.addVertex(tmp3.x() + currentX, tmp3.y() + currentY, tmp3.z() + currentZ).setUv(u0, v1).setColor(this.r, this.g, this.b, this.a).setLight(light);
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
            SnowParticle particle = new SnowParticle(clientLevel, x, y, z, vx, vy, vz);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}

