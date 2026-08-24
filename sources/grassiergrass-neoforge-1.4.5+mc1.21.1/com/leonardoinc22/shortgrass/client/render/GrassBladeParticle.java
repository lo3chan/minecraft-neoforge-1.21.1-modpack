package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.config.GrassConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

final class GrassBladeParticle extends TextureSheetParticle {
   private final float maxSize;
   private final float glideSpeed;
   private final float rollSpeed;

   GrassBladeParticle(ClientLevel level, double x, double y, double z, float glideSpeed, int tint, float maxSize, TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      this.setSprite(sprite);
      this.maxSize = maxSize;
      this.glideSpeed = glideSpeed;
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.lifetime = 10 + this.random.nextInt(13);
      this.rollSpeed = (0.1F + this.random.nextFloat() * 0.14F) * (this.random.nextBoolean() ? 1.0F : -1.0F);
      this.roll = this.random.nextFloat() * 6.2831855F;
      this.oRoll = this.roll;
      float brightness = GrassConfig.grassBrightness * 0.75F;
      this.setColor((tint >> 16 & 0xFF) / 255.0F * brightness, (tint >> 8 & 0xFF) / 255.0F * brightness, (tint & 0xFF) / 255.0F * brightness);
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
      } else {
         this.xd = GrassShaderUniforms.windDirX() * this.glideSpeed;
         this.yd = 0.0;
         this.zd = GrassShaderUniforms.windDirZ() * this.glideSpeed;
         this.move(this.xd, this.yd, this.zd);
         this.roll = this.roll + this.rollSpeed;
      }
   }

   public float getQuadSize(float partialTick) {
      float t = Mth.clamp((this.age + partialTick) / this.lifetime, 0.0F, 1.0F);
      return this.maxSize * Mth.sin(t * 3.1415927F);
   }
}
