package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.mixin.mixins.client.accessor.LevelRendererAccessor;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.DimensionSpecialEffects.OverworldEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class AetherSkyRenderEffects extends DimensionSpecialEffects {
   private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   private final DimensionSpecialEffects OVERWORLD = new OverworldEffects();
   private final float[] sunriseCol = new float[4];
   private int prevCloudX = -2147483648;
   private int prevCloudY = -2147483648;
   private int prevCloudZ = -2147483648;
   private Vec3 prevCloudColor = Vec3.ZERO;

   public AetherSkyRenderEffects() {
      super(9.5F, true, SkyType.NORMAL, false, false);
   }

   public void adjustLightmapColors(
      ClientLevel level, float partialTicks, float skyDarken, float skyLight, float blockLight, int pixelX, int pixelY, Vector3f colors
   ) {
      if ((Boolean)AetherConfig.CLIENT.colder_lightmap.get()) {
         Vector3f vector3f = new Vector3f(skyDarken, skyDarken, 1.0F).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
         Vector3f vector3f1 = new Vector3f();
         float f9 = LightTexture.getBrightness(level.dimensionType(), pixelX) * skyLight;
         float f10 = f9 * (f9 * f9 * 0.6F + 0.4F);
         vector3f1.set(f10, f10, f10);
         boolean flag = level.effects().forceBrightLightmap();
         if (flag) {
            vector3f1.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
            clampColor(vector3f1);
         } else {
            Vector3f vector3f2 = new Vector3f(vector3f).mul(blockLight);
            vector3f1.add(vector3f2);
            vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
            if (Minecraft.getInstance().gameRenderer.getDarkenWorldAmount(partialTicks) > 0.0F) {
               float darken = Minecraft.getInstance().gameRenderer.getDarkenWorldAmount(partialTicks);
               Vector3f vector3f3 = new Vector3f(vector3f1).mul(0.7F, 0.6F, 0.6F);
               vector3f1.lerp(vector3f3, darken);
            }
         }

         colors.set(vector3f1);
      }
   }

   private static void clampColor(Vector3f vector) {
      vector.set(Mth.clamp(vector.x(), 0.0F, 1.0F), Mth.clamp(vector.y(), 0.0F, 1.0F), Mth.clamp(vector.z(), 0.0F, 1.0F));
   }

   @Nullable
   public float[] getSunriseColor(float timeOfDay, float partialTicks) {
      if ((Boolean)AetherConfig.CLIENT.green_sunset.get()) {
         float f1 = Mth.cos(timeOfDay * 6.2831855F) - 0.0F;
         if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = (f1 + 0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - Mth.sin(f3 * 3.1415927F)) * 0.99F;
            f4 *= f4;
            this.sunriseCol[0] = f3 * 0.5F + 0.0F;
            this.sunriseCol[1] = f3 * f3 * 0.3F + 0.3F;
            this.sunriseCol[2] = f3 * f3 * 0.5F + 0.3F;
            this.sunriseCol[3] = f4;
            return this.sunriseCol;
         } else {
            return null;
         }
      } else {
         float f1 = Mth.cos(timeOfDay * 6.2831855F) - 0.0F;
         if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = (f1 + 0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - Mth.sin(f3 * 3.1415927F)) * 0.99F;
            f4 *= f4;
            this.sunriseCol[0] = f3 * 0.3F + 0.65F;
            this.sunriseCol[1] = f3 * f3 * 0.7F + 0.25F;
            this.sunriseCol[2] = f3 * f3 * 0.0F + 0.4F;
            this.sunriseCol[3] = f4;
            return this.sunriseCol;
         } else {
            return null;
         }
      }
   }

   public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
      return this.OVERWORLD.getBrightnessDependentFogColor(color, brightness);
   }

   public boolean isFoggyAt(int x, int z) {
      return this.OVERWORLD.isFoggyAt(x, z);
   }

   public boolean renderClouds(
      ClientLevel level,
      int ticks,
      float partialTick,
      PoseStack poseStack,
      double camX,
      double camY,
      double camZ,
      Matrix4f modelViewMatrix,
      Matrix4f projectionMatrix
   ) {
      if (!(Boolean)AetherConfig.CLIENT.disable_clouds.get()) {
         LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
         float cloudHeight = level.effects().getCloudHeight();
         if (!Float.isNaN(cloudHeight)) {
            double d1 = (ticks + partialTick) * 0.03F;
            double d2 = (camX + d1) / 12.0;
            double d3 = cloudHeight - (float)camY + 0.33F;
            double d4 = camZ / 12.0 + 0.33000001311302185;
            d2 -= Mth.floor(d2 / 2048.0) * 2048;
            d4 -= Mth.floor(d4 / 2048.0) * 2048;
            float f3 = (float)(d2 - Mth.floor(d2));
            float f4 = (float)(d3 / 4.0 - Mth.floor(d3 / 4.0)) * 4.0F;
            float f5 = (float)(d4 - Mth.floor(d4));
            Vec3 vec3 = this.getCloudColor(level, partialTick);
            int i = Mth.floor(d2);
            int j = Mth.floor(d3 / 4.0);
            int k = Mth.floor(d4);
            if (i != this.prevCloudX
               || j != this.prevCloudY
               || k != this.prevCloudZ
               || Minecraft.getInstance().options.getCloudsType() != ((LevelRendererAccessor)levelRenderer).aether$getPrevCloudsType()
               || this.prevCloudColor.distanceToSqr(vec3) > 2.0E-4) {
               this.prevCloudX = i;
               this.prevCloudY = j;
               this.prevCloudZ = k;
               this.prevCloudColor = vec3;
               ((LevelRendererAccessor)levelRenderer).aether$setPrevCloudsType(Minecraft.getInstance().options.getCloudsType());
               ((LevelRendererAccessor)levelRenderer).aether$setGenerateClouds(true);
            }

            if (((LevelRendererAccessor)levelRenderer).aether$isGenerateClouds()) {
               ((LevelRendererAccessor)levelRenderer).aether$setGenerateClouds(false);
               if (((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer() != null) {
                  ((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer().close();
               }

               ((LevelRendererAccessor)levelRenderer).aether$setCloudBuffer(new VertexBuffer(Usage.STATIC));
               ((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer().bind();
               ((LevelRendererAccessor)levelRenderer)
                  .aether$getCloudBuffer()
                  .upload(((LevelRendererAccessor)levelRenderer).callBuildClouds(Tesselator.getInstance(), d2, d3, d4, vec3));
               VertexBuffer.unbind();
            }

            FogRenderer.levelFogColor();
            poseStack.pushPose();
            poseStack.mulPose(modelViewMatrix);
            poseStack.scale(12.0F, 1.0F, 12.0F);
            poseStack.translate(-f3, f4, -f5);
            if (((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer() != null && RenderSystem.getShader() != null) {
               ((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer().bind();
               int l = ((LevelRendererAccessor)levelRenderer).aether$getPrevCloudsType() == CloudStatus.FANCY ? 0 : 1;

               for (int i1 = l; i1 < 2; i1++) {
                  RenderType rendertype = i1 == 0 ? RenderType.cloudsDepthOnly() : RenderType.clouds();
                  rendertype.setupRenderState();
                  ShaderInstance shaderinstance = RenderSystem.getShader();
                  ((LevelRendererAccessor)levelRenderer).aether$getCloudBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
                  rendertype.clearRenderState();
               }

               VertexBuffer.unbind();
            }

            poseStack.popPose();
         }
      }

      return true;
   }

   public Vec3 getCloudColor(ClientLevel level, float partialTick) {
      float f = level.getTimeOfDay(partialTick);
      float f1 = Mth.cos(f * 6.2831855F) * 2.0F + 0.5F;
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = level.getRainLevel(partialTick);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.725F;
         float f7 = 1.0F - f5 * 0.8F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      f2 *= f1 * 0.9F + 0.1F;
      f3 *= f1 * 0.9F + 0.1F;
      f4 *= f1 * 0.85F + 0.15F;
      float f9 = level.getThunderLevel(partialTick);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.5F;
         float f8 = 1.0F - f9 * 0.7F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vec3(f2, f3, f4);
   }

   public boolean renderSky(
      ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog
   ) {
      setupFog.run();
      if (!isFoggy) {
         FogType fogtype = camera.getFluidInCamera();
         if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(camera)) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(modelViewMatrix);
            LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
            Vec3 vec3 = this.getSkyColor(level, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), partialTick);
            float f = (float)vec3.x;
            float f1 = (float)vec3.y;
            float f2 = (float)vec3.z;
            FogRenderer.levelFogColor();
            Tesselator tesselator = Tesselator.getInstance();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(f, f1, f2, 1.0F);
            ShaderInstance shaderinstance = RenderSystem.getShader();
            ((LevelRendererAccessor)levelRenderer).aether$getSkyBuffer().bind();
            ((LevelRendererAccessor)levelRenderer).aether$getSkyBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
            VertexBuffer.unbind();
            RenderSystem.enableBlend();
            float[] sunriseColor = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
            if (sunriseColor != null) {
               RenderSystem.setShader(GameRenderer::getPositionColorShader);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               poseStack.pushPose();
               poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
               float f3 = Mth.sin(level.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F;
               poseStack.mulPose(Axis.ZP.rotationDegrees(f3));
               poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
               float f4 = sunriseColor[0];
               float f5 = sunriseColor[1];
               float f6 = sunriseColor[2];
               Matrix4f matrix4f = poseStack.last().pose();
               BufferBuilder sunriseBuffer = tesselator.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
               sunriseBuffer.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(f4, f5, f6, sunriseColor[3]);

               for (int j = 0; j <= 16; j++) {
                  float f7 = j * 6.2831855F / 16.0F;
                  float f8 = Mth.sin(f7);
                  float f9 = Mth.cos(f7);
                  sunriseBuffer.addVertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * sunriseColor[3])
                     .setColor(sunriseColor[0], sunriseColor[1], sunriseColor[2], 0.0F);
               }

               BufferUploader.drawWithShader(sunriseBuffer.buildOrThrow());
               poseStack.popPose();
            }

            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
            poseStack.pushPose();
            float f11 = 1.0F - level.getRainLevel(partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
            this.drawCelestialBodies(partialTick, poseStack, level, tesselator);
            float f10 = level.getStarBrightness(partialTick) * f11;
            if (f10 > 0.0F) {
               RenderSystem.setShaderColor(f10, f10, f10, f10);
               FogRenderer.setupNoFog();
               ((LevelRendererAccessor)levelRenderer).aether$getStarBuffer().bind();
               ((LevelRendererAccessor)levelRenderer)
                  .aether$getStarBuffer()
                  .drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
               VertexBuffer.unbind();
               setupFog.run();
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            poseStack.popPose();
            RenderSystem.depthMask(true);
         }
      }

      return true;
   }

   private boolean doesMobEffectBlockSky(Camera pCamera) {
      return !(pCamera.getEntity() instanceof LivingEntity livingentity)
         ? false
         : livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS);
   }

   public Vec3 getSkyColor(ClientLevel level, Vec3 pos, float partialTick) {
      float f = level.getTimeOfDay(partialTick);
      Vec3 vec3 = pos.subtract(2.0, 2.0, 2.0).scale(0.25);
      BiomeManager biomeManager = level.getBiomeManager();
      Vec3 vec31 = CubicSampler.gaussianSampleVec3(vec3, (x, y, z) -> Vec3.fromRGB24(((Biome)biomeManager.getNoiseBiomeAtQuart(x, y, z).value()).getSkyColor()));
      float f1 = Mth.cos(f * 6.2831855F) * 2.0F + 0.5F;
      f1 = Mth.clamp(f1, 0.0F, 1.0F);
      float f2 = (float)vec31.x() * f1;
      float f3 = (float)vec31.y() * f1;
      float f4 = (float)vec31.z() * f1;
      float f5 = level.getRainLevel(partialTick);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.61F;
         float f7 = 1.0F - f5 * 0.2F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      float f9 = level.getThunderLevel(partialTick);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.48F;
         float f8 = 1.0F - f9 * 0.21F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      if (!(Boolean)Minecraft.getInstance().options.hideLightningFlash().get() && level.getSkyFlashTime() > 0) {
         float f11 = level.getSkyFlashTime() - partialTick;
         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         f11 *= 0.45F;
         f2 = f2 * (1.0F - f11) + 0.8F * f11;
         f3 = f3 * (1.0F - f11) + 0.8F * f11;
         f4 = f4 * (1.0F - f11) + f11;
      }

      return new Vec3(f2, f3, f4);
   }

   private void drawCelestialBodies(float partialTick, PoseStack poseStack, ClientLevel level, Tesselator tesselator) {
      long dayTime = level.getDayTime() % AetherTimeAttachment.getTicksPerDay();
      float sunOpacity;
      float moonOpacity;
      if (dayTime > 23800L * AetherTimeAttachment.getTicksPerDayMultiplier()) {
         dayTime -= 23800L * AetherTimeAttachment.getTicksPerDayMultiplier();
         sunOpacity = Math.min((float)dayTime * 0.00167F, 1.0F);
         moonOpacity = Math.max(1.0F - (float)dayTime * 0.00167F, 0.0F);
      } else if (dayTime > 12800L * AetherTimeAttachment.getTicksPerDayMultiplier()) {
         dayTime -= 12800L * AetherTimeAttachment.getTicksPerDayMultiplier();
         sunOpacity = Math.max(1.0F - (float)dayTime * 0.00167F, 0.0F);
         moonOpacity = Math.min((float)dayTime * 0.00167F, 1.0F);
      } else {
         sunOpacity = 1.0F;
         moonOpacity = 0.0F;
      }

      sunOpacity -= level.getRainLevel(partialTick);
      moonOpacity -= level.getRainLevel(partialTick);
      poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
      Matrix4f matrix4f1 = poseStack.last().pose();
      float celestialOffset = 30.0F;
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, sunOpacity);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, SUN_LOCATION);
      BufferBuilder sunBuffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      sunBuffer.addVertex(matrix4f1, -celestialOffset, 100.0F, -celestialOffset).setUv(0.0F, 0.0F);
      sunBuffer.addVertex(matrix4f1, celestialOffset, 100.0F, -celestialOffset).setUv(1.0F, 0.0F);
      sunBuffer.addVertex(matrix4f1, celestialOffset, 100.0F, celestialOffset).setUv(1.0F, 1.0F);
      sunBuffer.addVertex(matrix4f1, -celestialOffset, 100.0F, celestialOffset).setUv(0.0F, 1.0F);
      BufferUploader.drawWithShader(sunBuffer.buildOrThrow());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, moonOpacity);
      celestialOffset = 20.0F;
      RenderSystem.setShaderTexture(0, MOON_LOCATION);
      int moonPhase = level.getMoonPhase();
      int textureX = moonPhase % 4;
      int textureY = moonPhase / 4 % 2;
      float uLeft = textureX / 4.0F;
      float vDown = textureY / 2.0F;
      float uRight = (textureX + 1) / 4.0F;
      float vUp = (textureY + 1) / 2.0F;
      BufferBuilder moonBuffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      moonBuffer.addVertex(matrix4f1, -celestialOffset, -100.0F, celestialOffset).setUv(uRight, vUp);
      moonBuffer.addVertex(matrix4f1, celestialOffset, -100.0F, celestialOffset).setUv(uLeft, vUp);
      moonBuffer.addVertex(matrix4f1, celestialOffset, -100.0F, -celestialOffset).setUv(uLeft, vDown);
      moonBuffer.addVertex(matrix4f1, -celestialOffset, -100.0F, -celestialOffset).setUv(uRight, vDown);
      BufferUploader.drawWithShader(moonBuffer.buildOrThrow());
   }
}
