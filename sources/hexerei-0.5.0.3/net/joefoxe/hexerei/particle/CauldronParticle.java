package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.awt.Color;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class CauldronParticle extends TextureSheetParticle {
   private final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/particle/cauldron_boil_particle.png");
   public static final Vec3[] CUBE = new Vec3[]{
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, -0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, 0.1, -0.5),
      new Vec3(-0.5, -0.1, -0.5),
      new Vec3(-0.5, -0.1, 0.5),
      new Vec3(-0.5, 0.1, 0.5),
      new Vec3(0.5, 0.1, 0.5),
      new Vec3(0.5, -0.1, 0.5),
      new Vec3(0.5, -0.1, -0.5),
      new Vec3(0.5, 0.1, -0.5)
   };
   public static final Vec3[] CUBE_NORMALS = new Vec3[]{
      new Vec3(0.0, -0.1, 0.0), new Vec3(0.0, 0.25, 0.0), new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, 0.0, -0.5), new Vec3(-0.5, 0.0, 0.0), new Vec3(0.5, 0.0, 0.0)
   };
   public static final ResourceLocation TEXTURE_BLANK = HexereiUtil.getResource("textures/block/blank.png");
   private static final ParticleRenderType renderType = new ParticleRenderType() {
      @Nullable
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, CauldronParticle.TEXTURE_BLANK);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }
   };
   protected float scale;
   protected float rotationDirection;
   protected float rotation;
   private IClientFluidTypeExtensions clientFluid;
   private boolean canPop;
   int pixelCol = -1;

   public CauldronParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.rotation = 0.0F;
      Random random = new Random();
      this.setScale(0.2F);
      this.setRotationDirection(random.nextFloat() - 0.5F);
      this.canPop = random.nextInt(3) == 0 && this.lifetime > 10;
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

   public float ease(float x) {
      return (float)(1.0 - Math.pow(1.0F - x, 5.0));
   }

   public void render(VertexConsumer builder, Camera renderInfo, float partial) {
      Vec3 projectedView = renderInfo.getPosition();
      float lerpX = (float)(Mth.lerp(partial, this.xo, this.x) - projectedView.x());
      float lerpY = (float)(Mth.lerp(partial, this.yo, this.y) - projectedView.y());
      float lerpZ = (float)(Mth.lerp(partial, this.zo, this.z) - projectedView.z());
      int light = 15728880;
      double ageMultiplier = 1.0 - Math.pow(Mth.clamp(this.age + partial, 0.0F, this.lifetime), 3.0) / Math.pow(this.lifetime, 3.0);
      RenderSystem._setShaderTexture(0, this.TEXTURE);

      for (int i = 0; i < 12; i++) {
         for (int j = 0; j < 4; j++) {
            float alpha = this.alpha * 0.75F;
            Vec3 vec = CUBE[i * 4 + j];
            float popScale = Math.clamp(this.age + partial - (this.lifetime - 5), 0.0F, 5.0F) / 5.0F;
            vec = vec.yRot(this.rotation)
               .scale(this.scale * ageMultiplier * (this.canPop && i % 6 != 0 && i % 6 != 1 ? 1.0F + this.ease(popScale) : 1.0F))
               .add(lerpX, lerpY, lerpZ);
            Vec3 normal = CUBE_NORMALS[i % 6];
            if (this.canPop && popScale > 0.0F) {
               vec = vec.add(normal.yRot(this.rotation).scale(this.ease(popScale) / 4.0F));
               alpha *= Math.clamp(this.ease(Mth.clamp(1.0F - popScale, 0.0F, 1.0F)), 0.0F, 1.0F);
            }

            float[] cols = HexereiUtil.rgbaIntToFloatArray(this.pixelCol);
            if (i % 6 == 1) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.35F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 1.35F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 1.35F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i % 6 == 0) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 0.95F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 0.95F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 0.95F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i % 6 == 2) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.15F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 1.15F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 1.15F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i % 6 == 3) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.2F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 1.2F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 1.2F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else if (i % 6 == 4) {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.25F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 1.25F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 1.25F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
                  .setNormal((float)normal.x, (float)normal.y, (float)normal.z)
                  .setLight(light);
            } else {
               builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                  .setUv(0.0F, 0.0F)
                  .setColor(
                     Mth.clamp(this.rCol * 1.2F * cols[2], 0.0F, 1.0F),
                     Mth.clamp(this.gCol * 1.2F * cols[1], 0.0F, 1.0F),
                     Mth.clamp(this.bCol * 1.2F * cols[0], 0.0F, 1.0F),
                     alpha * cols[3]
                  )
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
   public static class Factory implements ParticleProvider<CauldronParticleData> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(CauldronParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         CauldronParticle cauldronParticle = new CauldronParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         Random random = new Random();
         MixingCauldronTile mixingCauldronTile = null;
         FluidStack fluidStack = data.fluid;
         Color color = new Color(BiomeColors.getAverageWaterColor(worldIn, new BlockPos((int)x, (int)y, (int)z)));
         BlockState blockStateAtPos = worldIn.getBlockState(new BlockPos((int)x, (int)(y - 0.1), (int)z));
         cauldronParticle.clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());
         Function<ResourceLocation, TextureAtlasSprite> textureAtlasSpriteFunction = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
         ResourceLocation stillLoc = cauldronParticle.clientFluid.getStillTexture(fluidStack);
         TextureAtlasSprite sprite = textureAtlasSpriteFunction.apply(stillLoc);
         if (sprite != null) {
            cauldronParticle.pixelCol = sprite.getPixelRGBA(0, random.nextInt(sprite.contents().width()), random.nextInt(sprite.contents().height()));
         }

         int colorInt = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
         float alpha = (colorInt >> 24 & 0xFF) / 275.0F;
         float red = (colorInt >> 16 & 0xFF) / 275.0F;
         float green = (colorInt >> 8 & 0xFF) / 275.0F;
         float blue = (colorInt & 0xFF) / 275.0F;
         float colorOffset = random.nextFloat() * 0.15F;
         cauldronParticle.setColor(
            Mth.clamp(red + colorOffset, 0.0F, 1.0F), Mth.clamp(green + colorOffset, 0.0F, 1.0F), Mth.clamp(blue + colorOffset, 0.0F, 1.0F)
         );
         if (fluidStack.is(Fluids.WATER)) {
            cauldronParticle.setColor(color.getRed() / 450.0F + colorOffset, color.getGreen() / 450.0F + colorOffset, color.getBlue() / 450.0F + colorOffset);
         }

         cauldronParticle.setAlpha(1.0F);
         cauldronParticle.pickSprite(this.spriteSet);
         return cauldronParticle;
      }
   }
}
