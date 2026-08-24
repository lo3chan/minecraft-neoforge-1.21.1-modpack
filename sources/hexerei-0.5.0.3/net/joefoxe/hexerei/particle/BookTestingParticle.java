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
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BookTestingParticle extends TextureSheetParticle {
   public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/particle/cauldron_boil_particle.png");
   public static final Vector3f[] CUBE = new Vector3f[]{
      new Vector3f(-0.1F, -0.1F, -0.1F),
      new Vector3f(-0.1F, 0.1F, -0.1F),
      new Vector3f(0.1F, 0.1F, -0.1F),
      new Vector3f(0.1F, -0.1F, -0.1F),
      new Vector3f(0.1F, -0.1F, 0.1F),
      new Vector3f(0.1F, 0.1F, 0.1F),
      new Vector3f(-0.1F, 0.1F, 0.1F),
      new Vector3f(-0.1F, -0.1F, 0.1F),
      new Vector3f(0.1F, -0.1F, -0.1F),
      new Vector3f(0.1F, 0.1F, -0.1F),
      new Vector3f(0.1F, 0.1F, 0.1F),
      new Vector3f(0.1F, -0.1F, 0.1F),
      new Vector3f(-0.1F, -0.1F, 0.1F),
      new Vector3f(-0.1F, 0.1F, 0.1F),
      new Vector3f(-0.1F, 0.1F, -0.1F),
      new Vector3f(-0.1F, -0.1F, -0.1F),
      new Vector3f(0.1F, -0.1F, -0.1F),
      new Vector3f(0.1F, -0.1F, 0.1F),
      new Vector3f(-0.1F, -0.1F, 0.1F),
      new Vector3f(-0.1F, -0.1F, -0.1F),
      new Vector3f(-0.1F, 0.1F, -0.1F),
      new Vector3f(-0.1F, 0.1F, 0.1F),
      new Vector3f(0.1F, 0.1F, 0.1F),
      new Vector3f(0.1F, 0.1F, -0.1F)
   };
   public static final Vec3[] CUBE_NORMALS = new Vec3[]{new Vec3(0.0, 0.0, 0.10000000149011612), new Vec3(0.0, 0.0, 0.10000000149011612)};
   protected float scale;
   protected float rotationDirection;
   protected float rotation;
   protected float rotationOffsetYaw;
   protected float rotationOffsetPitch;
   protected float rotationOffsetRoll;
   protected float colorOffset;
   private static final ParticleRenderType renderType = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.setShaderTexture(0, BookTestingParticle.TEXTURE);
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         RenderSystem.disableDepthTest();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }
   };

   public BookTestingParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.rotation = 0.0F;
      this.lifetime = (int)motionX;
      Random random = new Random();
      this.colorOffset = random.nextFloat() * 0.26F;
      this.rotationOffsetYaw = random.nextFloat();
      this.rotationOffsetPitch = random.nextFloat();
      this.rotationOffsetRoll = random.nextFloat();
      this.setScale(0.02F);
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

   public void render(VertexConsumer builder, Camera renderInfo, float partialTicks) {
      Vec3 projectedView = renderInfo.getPosition();
      float lerpX = (float)(Mth.lerp(partialTicks, this.xo, this.x) - projectedView.x());
      float lerpY = (float)(Mth.lerp(partialTicks, this.yo, this.y) - projectedView.y());
      float lerpZ = (float)(Mth.lerp(partialTicks, this.zo, this.z) - projectedView.z());

      for (int i = 0; i < CUBE.length / 4; i++) {
         for (int j = 0; j < 4; j++) {
            Vector3f vec3f = CUBE[i * 4 + j];
            Vec3 vec = new Vec3(vec3f.x, vec3f.y, vec3f.z);
            vec = vec.yRot(this.rotation + this.rotationOffsetYaw)
               .xRot(this.rotation + this.rotationOffsetPitch)
               .zRot(this.rotation + this.rotationOffsetRoll)
               .scale(this.scale)
               .add(lerpX, lerpY, lerpZ);
            Vec3 normal = CUBE_NORMALS[0];
            builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
               .setUv(0.0F, 0.0F)
               .setColor(Mth.clamp(this.rCol, 0.0F, 1.0F), Mth.clamp(this.gCol, 0.0F, 1.0F), Mth.clamp(this.bCol, 0.0F, 1.0F), this.alpha)
               .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
               .setLight(15728880);
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
         BookTestingParticle cauldronParticle = new BookTestingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         Random random = new Random();
         float colorOffset = random.nextFloat() * 0.1F;
         cauldronParticle.setColor(0.5F, 0.5F, 1.0F);
         cauldronParticle.setAlpha(1.0F);
         cauldronParticle.pickSprite(this.spriteSet);
         return cauldronParticle;
      }
   }
}
