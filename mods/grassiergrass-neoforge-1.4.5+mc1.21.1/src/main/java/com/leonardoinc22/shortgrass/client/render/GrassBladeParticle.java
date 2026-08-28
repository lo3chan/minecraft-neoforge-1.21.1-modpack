/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.Mth
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassShaderUniforms;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

final class GrassBladeParticle
extends TextureSheetParticle {
    private final float maxSize;
    private final float glideSpeed;
    private final float rollSpeed;

    GrassBladeParticle(ClientLevel level, double x, double y, double z, float glideSpeed, int tint, float maxSize, TextureAtlasSprite sprite) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.setSprite(sprite);
        this.maxSize = maxSize;
        this.glideSpeed = glideSpeed;
        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.lifetime = 10 + this.random.nextInt(13);
        this.rollSpeed = (0.1f + this.random.nextFloat() * 0.14f) * (this.random.nextBoolean() ? 1.0f : -1.0f);
        this.oRoll = this.roll = this.random.nextFloat() * ((float)Math.PI * 2);
        float brightness = GrassConfig.grassBrightness * 0.75f;
        this.setColor((float)(tint >> 16 & 0xFF) / 255.0f * brightness, (float)(tint >> 8 & 0xFF) / 255.0f * brightness, (float)(tint & 0xFF) / 255.0f * brightness);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.xd = GrassShaderUniforms.windDirX() * this.glideSpeed;
        this.yd = 0.0;
        this.zd = GrassShaderUniforms.windDirZ() * this.glideSpeed;
        this.move(this.xd, this.yd, this.zd);
        this.roll += this.rollSpeed;
    }

    public float getQuadSize(float partialTick) {
        float t = Mth.clamp((float)(((float)this.age + partialTick) / (float)this.lifetime), (float)0.0f, (float)1.0f);
        return this.maxSize * Mth.sin((float)(t * (float)Math.PI));
    }
}

