package net.mehvahdjukaar.amendments.client.particles;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class FireballExplosionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected FireballExplosionParticle(ClientLevel clientLevel, double d, double e, double f, double size, SpriteSet spriteSet) {
      super(clientLevel, d, e, f, 0.0, 0.0, 0.0);
      this.lifetime = 5 + this.random.nextInt(3);
      this.quadSize = 2.0F * (1.0F - (float)size * 0.5F);
      this.sprites = spriteSet;
      this.bCol = 0.7F + this.random.nextFloat() * 0.3F;
      this.gCol = 0.9F + this.random.nextFloat() * 0.1F;
      this.setSpriteFromAge(spriteSet);
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   static class MutableSpriteSet implements SpriteSet {
      private final List<TextureAtlasSprite> sprites;

      public MutableSpriteSet(TextureAtlasSprite[] s1) {
         this.sprites = List.of(s1);
      }

      public TextureAtlasSprite get(int age, int lifetime) {
         return this.sprites.get(age * (this.sprites.size() - 1) / lifetime);
      }

      public TextureAtlasSprite get(RandomSource random) {
         return this.sprites.get(random.nextInt(this.sprites.size()));
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final Supplier<SpriteSet> sprites1;
      private final Supplier<SpriteSet> sprites2;
      private final Supplier<SpriteSet> sprites3;

      public Provider(SpriteSet sprites) {
         int masSprites = 15;
         this.sprites1 = Suppliers.memoize(
            () -> new FireballExplosionParticle.MutableSpriteSet(
               new TextureAtlasSprite[]{
                  sprites.get(1, masSprites), sprites.get(2, masSprites), sprites.get(3, masSprites), sprites.get(4, masSprites), sprites.get(5, masSprites)
               }
            )
         );
         this.sprites2 = Suppliers.memoize(
            () -> new FireballExplosionParticle.MutableSpriteSet(
               new TextureAtlasSprite[]{
                  sprites.get(6, masSprites), sprites.get(7, masSprites), sprites.get(8, masSprites), sprites.get(9, masSprites), sprites.get(10, masSprites)
               }
            )
         );
         this.sprites3 = Suppliers.memoize(
            () -> new FireballExplosionParticle.MutableSpriteSet(
               new TextureAtlasSprite[]{
                  sprites.get(11, masSprites),
                  sprites.get(12, masSprites),
                  sprites.get(13, masSprites),
                  sprites.get(14, masSprites),
                  sprites.get(15, masSprites)
               }
            )
         );
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         int i = level.random.nextInt(2);
         if (i == 0) {
            return new FireballExplosionParticle(level, x, y, z, xSpeed, this.sprites1.get());
         } else {
            return i == 1
               ? new FireballExplosionParticle(level, x, y, z, xSpeed, this.sprites2.get())
               : new FireballExplosionParticle(level, x, y, z, xSpeed, this.sprites3.get());
         }
      }
   }
}
