package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Random;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodParticle extends TextureSheetParticle {
   private final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/particle/cauldron_boil_particle.png");
   public static final Vec3[] CUBE = new Vec3[]{
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.25, -0.1, 0.5),
      new Vec3(0.25, -0.1, -0.5),
      new Vec3(-0.25, -0.1, -0.5),
      new Vec3(-0.25, -0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(0.25, -0.1, -0.5),
      new Vec3(0.25, -0.1, -0.25),
      new Vec3(-0.25, -0.1, -0.25),
      new Vec3(-0.25, -0.1, -0.5),
      new Vec3(-0.25, -0.1, 0.5),
      new Vec3(-0.25, -0.1, 0.25),
      new Vec3(0.25, -0.1, 0.25),
      new Vec3(0.25, -0.1, 0.5),
      new Vec3(0.25, 0.1, -0.5),
      new Vec3(0.25, 0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.25, 0.1, 0.5),
      new Vec3(-0.25, 0.1, -0.5),
      new Vec3(-0.25, 0.1, -0.5),
      new Vec3(-0.25, 0.1, -0.25),
      new Vec3(0.25, 0.1, -0.25),
      new Vec3(0.25, 0.1, -0.5),
      new Vec3(0.25, 0.1, 0.5),
      new Vec3(0.25, 0.1, 0.25),
      new Vec3(-0.25, 0.1, 0.25),
      new Vec3(-0.25, 0.1, 0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.25, -0.1, 0.25),
      new Vec3(-0.25, 0.1, 0.25),
      new Vec3(0.25, 0.1, 0.25),
      new Vec3(0.25, -0.1, 0.25),
      new Vec3(0.25, -0.1, -0.25),
      new Vec3(0.25, 0.1, -0.25),
      new Vec3(-0.25, 0.1, -0.25),
      new Vec3(-0.25, -0.1, -0.25),
      new Vec3(-0.25, -0.1, -0.25),
      new Vec3(-0.25, 0.1, -0.25),
      new Vec3(-0.25, 0.1, 0.25),
      new Vec3(-0.25, -0.1, 0.25),
      new Vec3(0.25, -0.1, 0.25),
      new Vec3(0.25, 0.1, 0.25),
      new Vec3(0.25, 0.1, -0.25),
      new Vec3(0.25, -0.1, -0.25),
      new Vec3(0.25, -0.01, -0.25),
      new Vec3(0.25, -0.01, 0.25),
      new Vec3(-0.25, -0.01, 0.25),
      new Vec3(-0.25, -0.01, -0.25),
      new Vec3(-0.25, 0.01, -0.25),
      new Vec3(-0.25, 0.01, 0.25),
      new Vec3(0.25, 0.01, 0.25),
      new Vec3(0.25, 0.01, -0.25)
   };
   public static final Vec3[] CUBE_NORMALS = new Vec3[]{
      new Vec3(0.0, 0.1, 0.0),
      new Vec3(0.0, 0.1, 0.0),
      new Vec3(0.0, 0.1, 0.0),
      new Vec3(0.0, 0.1, 0.0),
      new Vec3(0.0, -0.5, 0.0),
      new Vec3(0.0, -0.5, 0.0),
      new Vec3(0.0, -0.5, 0.0),
      new Vec3(0.0, -0.5, 0.0),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5),
      new Vec3(0.0, 0.0, 0.5)
   };
   public static final ResourceLocation TEXTURE_BLANK = HexereiUtil.getResource("textures/block/blank.png");
   private static final ParticleRenderType renderType = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.setShaderTexture(0, BloodParticle.TEXTURE_BLANK);
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }
   };
   protected float scale;
   protected float rotationDirection;
   protected float rotation;
   protected float rotationOffsetYaw;
   protected float rotationOffsetPitch;
   protected float rotationOffsetRoll;
   protected float colorOffset;

   public BloodParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.rotation = 0.0F;
      this.averageAge(80);
      Random random = new Random();
      this.colorOffset = random.nextFloat() * 0.25F;
      this.rotationOffsetYaw = random.nextFloat();
      this.rotationOffsetPitch = random.nextFloat();
      this.rotationOffsetRoll = random.nextFloat();
      this.setScale(0.2F);
      this.setRotationDirection(random.nextFloat() - 0.5F);
   }

   public void setScale(float scale) {
      this.scale = scale;
      this.setSize(scale * 0.5F, scale * 0.5F);
   }

   public void averageAge(int age) {
      Random random = new Random();
      this.lifetime = (int)(age + (random.nextDouble() * 2.0 - 1.0) * 8.0);
   }

   public void setRotationDirection(float rotationDirection) {
      this.rotationDirection = rotationDirection;
   }

   public void tick() {
      this.rotation = this.rotationDirection * 0.1F + this.rotation;
      super.tick();
   }

   public void render(VertexConsumer builder, Camera renderInfo, float p_225606_3_) {
      Vec3 projectedView = renderInfo.getPosition();
      float lerpX = (float)(Mth.lerp(p_225606_3_, this.xo, this.x) - projectedView.x());
      float lerpY = (float)(Mth.lerp(p_225606_3_, this.yo, this.y) - projectedView.y());
      float lerpZ = (float)(Mth.lerp(p_225606_3_, this.zo, this.z) - projectedView.z());
      int light = 15728880;
      double ageMultiplier = 1.0 - Math.pow(Mth.clamp(this.age + p_225606_3_, 0.0F, this.lifetime), 3.0) / Math.pow(this.lifetime, 3.0);
      RenderSystem._setShaderTexture(0, this.TEXTURE);

      for (int i = 0; i < CUBE.length / 4; i++) {
         for (int j = 0; j < 4; j++) {
            Vec3 vec = CUBE[i * 4 + j];
            vec = vec.yRot(this.rotation + this.rotationOffsetYaw)
               .xRot(this.rotation + this.rotationOffsetPitch)
               .zRot(this.rotation + this.rotationOffsetRoll)
               .scale(this.scale * ageMultiplier)
               .add(lerpX, lerpY, lerpZ);
            Vec3 normal = CUBE_NORMALS[i];
            if (i == 0 || i == 1 || i == 2 || i == 3) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.25F, 0.0F, 1.0F), Mth.clamp(this.gCol * 1.25F, 0.0F, 1.0F), Mth.clamp(this.bCol * 1.25F, 0.0F, 1.0F), this.alpha
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 4 || i == 5 || i == 6 || i == 7) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.55F, this.gCol * 0.55F, this.bCol * 0.55F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 8) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.75F, this.gCol * 0.95F, this.bCol * 0.95F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 9) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.85F, this.gCol * 0.75F, this.bCol * 0.75F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 10) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.95F, this.gCol * 0.9F, this.bCol * 0.9F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 11) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 1.05F, this.gCol * 1.05F, this.bCol * 1.05F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 12 || i == 13 || i == 14 || i == 15) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.4F, this.gCol * 0.4F, this.bCol * 0.4F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i != 17 && i != 16) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.85F, this.gCol * 0.85F, this.bCol * 0.85F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.4F, this.gCol * 0.4F, this.bCol * 0.4F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            }
         }
      }
   }

   public ParticleRenderType getRenderType() {
      return renderType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         BloodParticle cauldronParticle = new BloodParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         Random random = new Random();
         float colorOffset = random.nextFloat() * 0.2F;
         cauldronParticle.setColor(0.05F + colorOffset, 0.05F, 0.05F);
         cauldronParticle.setAlpha(1.0F);
         cauldronParticle.pickSprite(this.spriteSet);
         return cauldronParticle;
      }
   }
}
