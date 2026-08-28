/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.RandomSource
 */
package net.diebuddies.minecraft.weather;

import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public abstract class FastTextureSheetParticle
extends Particle {
    protected TextureAtlasSprite sprite;
    protected float quadSize;

    protected FastTextureSheetParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    protected FastTextureSheetParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(clientLevel, d, e, f, g, h, i);
    }

    protected void setSprite(TextureAtlasSprite textureAtlasSprite) {
        this.sprite = textureAtlasSprite;
    }

    protected float getU0() {
        return this.sprite.getU0();
    }

    protected float getU1() {
        return this.sprite.getU1();
    }

    protected float getV0() {
        return this.sprite.getV0();
    }

    protected float getV1() {
        return this.sprite.getV1();
    }

    public float getQuadSize() {
        return this.quadSize;
    }

    public void pickSprite(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get((RandomSource)Math.fastRandomSource));
    }
}

