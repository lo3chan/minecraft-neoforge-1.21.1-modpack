package io.github.razordevs.deep_aether.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle.FacingCameraMode;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class EOTSExplosionParticle extends SimpleAnimatedParticle {
   float yDegrees = 0.0F;
   private final FacingCameraMode NO_CAMERA_ROTATION_DOWNWARDS_ROTATING = (quaternionf, camera, partialTicks) -> {
      this.yDegrees += partialTicks;
      this.yDegrees = Mth.wrapDegrees(this.yDegrees);
      float xw = Mth.lerp(Mth.abs(this.yDegrees) / 180.0F, 0.0F, 0.707F);
      float yz = 0.707F - xw;
      if (camera.getPosition().y > this.getPos().y) {
         if (this.yDegrees < 0.0F) {
            quaternionf.set(-xw, -yz, -yz, xw);
         } else {
            quaternionf.set(-xw, yz, yz, xw);
         }
      } else if (this.yDegrees < 0.0F) {
         quaternionf.set(xw, yz, -yz, xw);
      } else {
         quaternionf.set(xw, -yz, yz, xw);
      }
   };

   protected EOTSExplosionParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY + 0.1, pZ, pSprites, 0.0F);
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      this.lifetime = 50;
      this.setFadeColor(15916745);
      this.setSpriteFromAge(pSprites);
      this.setSize(0.1F, 0.1F);
   }

   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      super.render(pBuffer, pRenderInfo, pPartialTicks);
      this.quadSize += pPartialTicks * pPartialTicks * 0.5F;
   }

   public FacingCameraMode getFacingCameraMode() {
      return this.NO_CAMERA_ROTATION_DOWNWARDS_ROTATING;
   }

   public AABB getBoundingBox() {
      return AABB.INFINITE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet spriteSet) {
         this.sprites = spriteSet;
      }

      @Nullable
      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         return new EOTSExplosionParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
