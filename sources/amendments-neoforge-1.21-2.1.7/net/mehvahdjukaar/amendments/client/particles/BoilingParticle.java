package net.mehvahdjukaar.amendments.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FastColor.ARGB32;

public class BoilingParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final TextureAtlasSprite shineSprite;
   private final double waterLevel;
   private final double growFactor;
   private final int lightLevel;
   private int maxTry = 200;

   BoilingParticle(ClientLevel level, double x, double y, double z, double waterLevel, int lightLevel, SpriteSet sprites) {
      super(level, x, y, z);
      this.setSize(0.0625F, 0.0625F);
      this.quadSize = this.quadSize * (this.random.nextFloat() * 0.4F + 0.16F);
      this.xd = (level.random.nextFloat() * 2.0 - 1.0) * 0.005;
      this.yd = (level.random.nextFloat() * 2.0 - 1.0) * 0.005;
      this.zd = (level.random.nextFloat() * 2.0 - 1.0) * 0.005;
      this.lifetime = (int)MthUtils.nextWeighted(level.random, 26.0F, 1.0F, 5.0F);
      this.sprites = sprites;
      this.setSpriteFromAge(this.sprites);
      this.shineSprite = sprites.get(0, 6);
      this.waterLevel = waterLevel;
      this.growFactor = 0.002 + level.random.nextFloat() * 0.004;
      this.lightLevel = lightLevel;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.maxTry-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (this.y < this.waterLevel) {
            this.yd += 0.002;
         } else {
            this.y = this.waterLevel;
            this.quadSize = (float)(this.quadSize + this.growFactor);
            this.yd = 0.0;
            if (this.lifetime-- <= 0) {
               this.remove();
               return;
            }
         }

         this.yd *= 0.995;
         this.xd *= 0.98;
         this.zd *= 0.98;
         if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).is(BlockTags.CAULDRONS)) {
            this.remove();
         }

         this.setSpriteFromAge(this.sprites);
      }
   }

   public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
      if (this.lifetime - this.age >= 4) {
         TextureAtlasSprite old = this.sprite;
         float oldRed = this.rCol;
         float oldGreen = this.gCol;
         float oldBlue = this.bCol;
         this.setColor(1.0F, 1.0F, 1.0F);
         this.setSprite(this.shineSprite);
         super.render(buffer, renderInfo, partialTicks);
         this.setColor(oldRed, oldGreen, oldBlue);
         this.setSprite(old);
      }

      super.render(buffer, renderInfo, partialTicks);
   }

   protected int getLightColor(float partialTick) {
      int lightColor = super.getLightColor(partialTick);
      int sky = LightTexture.sky(lightColor);
      int block = LightTexture.block(lightColor);
      return LightTexture.pack(Math.max(this.lightLevel, block), sky);
   }

   public void setSpriteFromAge(SpriteSet sprite) {
      if (!this.removed) {
         int newAge = Math.max(1, 5 - (this.lifetime - this.age));
         this.setSprite(sprite.get(newAge, 5));
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprites) {
         this.sprite = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double endY, double z, double color, double startY, double light) {
         int intColor = (int)color;
         float r = ARGB32.red(intColor) / 255.0F;
         float g = ARGB32.green(intColor) / 255.0F;
         float b = ARGB32.blue(intColor) / 255.0F;
         BoilingParticle particle = new BoilingParticle(level, x, startY, z, endY, (int)light, this.sprite);
         particle.setColor(r, g, b);
         return particle;
      }
   }
}
