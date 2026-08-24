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
public class DowsingRodParticle extends TextureSheetParticle {
   private final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/particle/cauldron_boil_particle.png");
   public static final Vec3[] CUBE = new Vec3[]{
      new Vec3(0.5, 0.5, -0.5),
      new Vec3(0.5, 0.5, 0.5),
      new Vec3(-0.5, 0.5, 0.5),
      new Vec3(-0.5, 0.5, -0.5),
      new Vec3(-0.5, -0.5, -0.5),
      new Vec3(-0.5, -0.5, 0.5),
      new Vec3(0.5, -0.5, 0.5),
      new Vec3(0.5, -0.5, -0.5),
      new Vec3(-0.5, -0.5, 0.5),
      new Vec3(-0.5, 0.5, 0.5),
      new Vec3(0.5, 0.5, 0.5),
      new Vec3(0.5, -0.5, 0.5),
      new Vec3(0.5, -0.5, -0.5),
      new Vec3(0.5, 0.5, -0.5),
      new Vec3(-0.5, 0.5, -0.5),
      new Vec3(-0.5, -0.5, -0.5),
      new Vec3(-0.5, -0.5, -0.5),
      new Vec3(-0.5, 0.5, -0.5),
      new Vec3(-0.5, 0.5, 0.5),
      new Vec3(-0.5, -0.5, 0.5),
      new Vec3(0.5, -0.5, 0.5),
      new Vec3(0.5, 0.5, 0.5),
      new Vec3(0.5, 0.5, -0.5),
      new Vec3(0.5, -0.5, -0.5)
   };
   public static final Vec3[] CUBE_NORMALS = new Vec3[]{
      new Vec3(0.0, 0.5, 0.0), new Vec3(0.0, -0.5, 0.0), new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, 0.0, 0.5)
   };
   public static final ResourceLocation TEXTURE_BLANK = HexereiUtil.getResource("textures/block/blank.png");
   private static final ParticleRenderType renderType = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.setShaderTexture(0, DowsingRodParticle.TEXTURE_BLANK);
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }
   };
   protected float scale;
   protected float rotationDirection;
   protected float rotation;

   public DowsingRodParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.rotation = 0.0F;
      Random random = new Random();
      this.setScale(0.2F);
      this.setRotationDirection(random.nextFloat() - 0.5F);
   }

   public void setScale(float scale) {
      this.scale = scale;
      this.setSize(scale * 0.5F, scale * 0.5F);
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
      double ageMultiplier = 1.0 - Math.pow(Mth.clamp(this.age + p_225606_3_, 0.0F, this.lifetime), 3.0) / Math.pow(this.lifetime, 3.0) / 2.0;
      RenderSystem._setShaderTexture(0, this.TEXTURE);

      for (int i = 0; i < 6; i++) {
         for (int j = 0; j < 4; j++) {
            Vec3 vec = CUBE[i * 4 + j];
            vec = vec.yRot(this.rotation).scale(this.scale * ageMultiplier).add(lerpX, lerpY, lerpZ);
            Vec3 normal = CUBE_NORMALS[i];
            if (i == 0) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.25F, 0.0F, 1.0F), Mth.clamp(this.gCol * 1.25F, 0.0F, 1.0F), Mth.clamp(this.bCol * 1.25F, 0.0F, 1.0F), this.alpha
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 1) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.55F, this.gCol * 0.55F, this.bCol * 0.55F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 2) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.95F, this.gCol * 0.95F, this.bCol * 0.95F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 3) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.75F, this.gCol * 0.75F, this.bCol * 0.75F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i == 4) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.9F, this.gCol * 0.9F, this.bCol * 0.9F, this.alpha)
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(this.rCol * 0.85F, this.gCol * 0.85F, this.bCol * 0.85F, this.alpha)
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
         DowsingRodParticle cauldronParticle = new DowsingRodParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         Random random = new Random();
         float colorOffset = random.nextFloat() * 0.25F;
         cauldronParticle.setColor(colorOffset + 0.3F, colorOffset + 0.55F, colorOffset + 0.3F);
         cauldronParticle.setAlpha(1.0F);
         cauldronParticle.setScale(0.5F);
         cauldronParticle.setLifetime(1);
         cauldronParticle.pickSprite(this.spriteSet);
         return cauldronParticle;
      }
   }
}
