package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.AMVertex;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleStaticSpark extends Particle {
   private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
      AMCompat.rl("textures/particle/generic_0.png"),
      AMCompat.rl("textures/particle/generic_1.png"),
      AMCompat.rl("textures/particle/generic_2.png"),
      AMCompat.rl("textures/particle/generic_3.png"),
      AMCompat.rl("textures/particle/generic_4.png"),
      AMCompat.rl("textures/particle/generic_5.png"),
      AMCompat.rl("textures/particle/generic_6.png"),
      AMCompat.rl("textures/particle/generic_7.png")
   };
   private static final ResourceLocation[][] STATIC_SPARK_TEXTURES = new ResourceLocation[8][4];
   private int decrement = 1;
   private int textureIndex = 0;
   private float size;

   private ParticleStaticSpark(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.setSize(1.0F, 1.0F);
      this.gravity = 0.0F;
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.lifetime = 10 + this.random.nextInt(15);
      this.textureIndex = Mth.clamp(this.random.nextInt(8), 0, 7);
      this.decrement = this.textureIndex > 0 ? this.lifetime / this.textureIndex : this.lifetime;
      this.size = this.random.nextFloat() * 0.2F + 0.2F;
   }

   public void tick() {
      super.tick();
      this.xd *= 0.97;
      this.yd *= 0.97;
      this.zd *= 0.97;
      if (this.textureIndex > 0 && this.age % this.decrement == 0) {
         this.textureIndex--;
      }

      if (this.size > 0.2F) {
         this.size -= 0.015F;
      }
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
      Vec3 vec3 = camera.getPosition();
      float f = (float)(Mth.lerp(partialTick, this.xo, this.x) - vec3.x());
      float f1 = (float)(Mth.lerp(partialTick, this.yo, this.y) - vec3.y());
      float f2 = (float)(Mth.lerp(partialTick, this.zo, this.z) - vec3.z());
      Quaternionf quaternion;
      if (this.roll == 0.0F) {
         quaternion = camera.rotation();
      } else {
         quaternion = new Quaternionf(camera.rotation());
         float f3 = Mth.lerp(partialTick, this.oRoll, this.roll);
         quaternion.mul(Axis.ZP.rotation(f3));
      }

      BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      PoseStack posestack = new PoseStack();
      Pose posestack$pose = posestack.last();
      Matrix3f matrix3f = posestack$pose.normal();
      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.rotate(quaternion);
      Vector3f[] avector3f = new Vector3f[]{
         new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
      };
      float f4 = this.size;

      for (int i = 0; i < 4; i++) {
         Vector3f vector3f = avector3f[i];
         vector3f.rotate(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      float f7 = 0.0F;
      float f8 = 1.0F;
      float f5 = 0.0F;
      float f6 = 1.0F;
      int j = 240;
      AMRenderTypes.renderStaticMasked(
         multibuffersource$buffersource,
         AMRenderTypes.STATIC_PARTICLE,
         TEXTURES[this.textureIndex],
         STATIC_SPARK_TEXTURES[this.textureIndex][this.age / 2 % 4],
         portalStatic -> {
            AMVertex.normal(
               portalStatic.addVertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z())
                  .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                  .setUv(f8, f6)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(j),
               matrix3f,
               0.0F,
               -1.0F,
               0.0F
            );
            AMVertex.normal(
               portalStatic.addVertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z())
                  .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                  .setUv(f8, f5)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(j),
               matrix3f,
               0.0F,
               -1.0F,
               0.0F
            );
            AMVertex.normal(
               portalStatic.addVertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z())
                  .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                  .setUv(f7, f5)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(j),
               matrix3f,
               0.0F,
               -1.0F,
               0.0F
            );
            AMVertex.normal(
               portalStatic.addVertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z())
                  .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                  .setUv(f7, f6)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(j),
               matrix3f,
               0.0F,
               -1.0F,
               0.0F
            );
         }
      );
      multibuffersource$buffersource.endBatch();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   static {
      for (int n = 0; n < 8; n++) {
         for (int v = 0; v < 4; v++) {
            STATIC_SPARK_TEXTURES[n][v] = AMCompat.rl("alexsmobs:textures/particle/static_spark_" + n + "_" + v + ".png");
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleStaticSpark(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
