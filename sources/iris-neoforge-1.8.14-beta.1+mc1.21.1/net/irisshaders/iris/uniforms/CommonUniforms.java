package net.irisshaders.iris.uniforms;

import com.mojang.blaze3d.platform.GlStateManager.BlendState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.compat.dh.DHCompat;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.state.StateUpdateNotifiers;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.layer.GbufferPrograms;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;
import net.irisshaders.iris.mixin.statelisteners.BooleanStateAccessor;
import net.irisshaders.iris.mixin.texture.TextureAtlasAccessor;
import net.irisshaders.iris.mixinterface.LocalPlayerInterface;
import net.irisshaders.iris.pbr.TextureInfoCache;
import net.irisshaders.iris.pbr.TextureTracker;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.transforms.SmoothedFloat;
import net.irisshaders.iris.uniforms.transforms.SmoothedVec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.joml.Vector4i;

public final class CommonUniforms {
   private static final Minecraft client = Minecraft.getInstance();
   private static final Vector2i ZERO_VECTOR_2i = new Vector2i();
   private static final Vector4i ZERO_VECTOR_4i = new Vector4i(0, 0, 0, 0);
   private static final Vector3d ZERO_VECTOR_3d = new Vector3d();

   private CommonUniforms() {
   }

   public static void addDynamicUniforms(DynamicUniformHolder uniforms, FogMode fogMode) {
      ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
      FogUniforms.addFogUniforms(uniforms, fogMode);
      IrisInternalUniforms.addFogUniforms(uniforms, fogMode);
      uniforms.uniform1i("entityId", CapturedRenderingState.INSTANCE::getCurrentRenderedEntity, StateUpdateNotifiers.fallbackEntityNotifier);
      uniforms.uniform2i("atlasSize", () -> {
         int glId = RenderSystem.getShaderTexture(0);
         if (TextureTracker.INSTANCE.getTexture(glId) instanceof TextureAtlas atlas) {
            TextureAtlasAccessor atlasAccessor = (TextureAtlasAccessor)atlas;
            return new Vector2i(atlasAccessor.callGetWidth(), atlasAccessor.callGetHeight());
         } else {
            return ZERO_VECTOR_2i;
         }
      }, listener -> {});
      uniforms.uniform2i("gtextureSize", () -> {
         int glId = GlStateManagerAccessor.getTEXTURES()[0].binding;
         TextureInfoCache.TextureInfo info = TextureInfoCache.INSTANCE.getInfo(glId);
         return new Vector2i(info.getWidth(), info.getHeight());
      }, StateUpdateNotifiers.bindTextureNotifier);
      uniforms.uniform4i("blendFunc", () -> {
         BlendState blend = GlStateManagerAccessor.getBLEND();
         return ((BooleanStateAccessor)blend.mode).isEnabled() ? new Vector4i(blend.srcRgb, blend.dstRgb, blend.srcAlpha, blend.dstAlpha) : ZERO_VECTOR_4i;
      }, StateUpdateNotifiers.blendFuncNotifier);
      uniforms.uniform1i("renderStage", () -> GbufferPrograms.getCurrentPhase().ordinal(), StateUpdateNotifiers.phaseChangeNotifier);
   }

   public static void addCommonUniforms(
      DynamicUniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier, FogMode fogMode
   ) {
      addNonDynamicUniforms(uniforms, idMap, directives, updateNotifier);
      addDynamicUniforms(uniforms, fogMode);
   }

   public static void addNonDynamicUniforms(UniformHolder uniforms, IdMap idMap, PackDirectives directives, FrameUpdateNotifier updateNotifier) {
      CameraUniforms.addCameraUniforms(uniforms, updateNotifier);
      ViewportUniforms.addViewportUniforms(uniforms);
      WorldTimeUniforms.addWorldTimeUniforms(uniforms);
      SystemTimeUniforms.addSystemTimeUniforms(uniforms);
      BiomeUniforms.addBiomeUniforms(uniforms);
      new CelestialUniforms(directives.getSunPathRotation()).addCelestialUniforms(uniforms);
      IrisExclusiveUniforms.addIrisExclusiveUniforms(uniforms);
      IrisTimeUniforms.addTimeUniforms(uniforms);
      MatrixUniforms.addMatrixUniforms(uniforms, directives);
      IdMapUniforms.addIdMapUniforms(updateNotifier, uniforms, idMap, directives.isOldHandLight());
      generalCommonUniforms(uniforms, updateNotifier, directives);
   }

   public static void generalCommonUniforms(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, PackDirectives directives) {
      ExternallyManagedUniforms.addExternallyManagedUniforms117(uniforms);
      SmoothedVec2f eyeBrightnessSmooth = new SmoothedVec2f(
         directives.getEyeBrightnessHalfLife(), directives.getEyeBrightnessHalfLife(), CommonUniforms::getEyeBrightness, updateNotifier
      );
      uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hideGUI", () -> client.options.hideGui)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "isRightHanded", () -> client.options.mainHand().get() == HumanoidArm.RIGHT)
         .uniform1i(UniformUpdateFrequency.PER_FRAME, "isEyeInWater", CommonUniforms::isEyeInWater)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "blindness", CommonUniforms::getBlindness)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessFactor", CommonUniforms::getDarknessFactor)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "darknessLightFactor", CapturedRenderingState.INSTANCE::getDarknessLightFactor)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "nightVision", CommonUniforms::getNightVision)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sneaking", CommonUniforms::isSneaking)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_sprinting", CommonUniforms::isSprinting)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_hurt", CommonUniforms::isHurt)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_invisible", CommonUniforms::isInvisible)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_burning", CommonUniforms::isBurning)
         .uniform1b(UniformUpdateFrequency.PER_FRAME, "is_on_ground", CommonUniforms::isOnGround)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "screenBrightness", () -> (Double)client.options.gamma().get())
         .uniform4f(UniformUpdateFrequency.ONCE, "entityColor", () -> new Vector4f(0.0F, 0.0F, 0.0F, 0.0F))
         .uniform1i(UniformUpdateFrequency.ONCE, "blockEntityId", () -> -1)
         .uniform1i(UniformUpdateFrequency.ONCE, "currentRenderedItemId", () -> -1)
         .uniform1f(UniformUpdateFrequency.ONCE, "pi", () -> 3.141592653589793)
         .uniform1f(UniformUpdateFrequency.PER_TICK, "playerMood", CommonUniforms::getPlayerMood)
         .uniform1f(UniformUpdateFrequency.PER_TICK, "constantMood", CommonUniforms::getConstantMood)
         .uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightness", CommonUniforms::getEyeBrightness)
         .uniform2i(UniformUpdateFrequency.PER_FRAME, "eyeBrightnessSmooth", () -> {
            Vector2f smoothed = eyeBrightnessSmooth.get();
            return new Vector2i((int)smoothed.x(), (int)smoothed.y());
         })
         .uniform1f(UniformUpdateFrequency.PER_TICK, "rainStrength", CommonUniforms::getRainStrength)
         .uniform1f(
            UniformUpdateFrequency.PER_TICK,
            "wetness",
            new SmoothedFloat(directives.getWetnessHalfLife(), directives.getDrynessHalfLife(), CommonUniforms::getRainStrength, updateNotifier)
         )
         .uniform3d(UniformUpdateFrequency.PER_FRAME, "skyColor", CommonUniforms::getSkyColor)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "dhFarPlane", DHCompat::getFarPlane)
         .uniform1f(UniformUpdateFrequency.PER_FRAME, "dhNearPlane", DHCompat::getNearPlane)
         .uniform1i(UniformUpdateFrequency.PER_FRAME, "dhRenderDistance", DHCompat::getRenderDistance);
   }

   private static boolean isOnGround() {
      return client.player != null && client.player.onGround();
   }

   private static boolean isHurt() {
      return client.player != null ? client.player.hurtTime > 0 : false;
   }

   private static boolean isInvisible() {
      return client.player != null ? client.player.isInvisible() : false;
   }

   private static boolean isBurning() {
      return client.player != null ? client.player.isOnFire() : false;
   }

   private static boolean isSneaking() {
      return client.player != null ? client.player.isCrouching() : false;
   }

   private static boolean isSprinting() {
      return client.player != null ? client.player.isSprinting() : false;
   }

   private static Vector3d getSkyColor() {
      return client.level != null && client.cameraEntity != null
         ? JomlConversions.fromVec3(client.level.getSkyColor(client.cameraEntity.position(), CapturedRenderingState.INSTANCE.getTickDelta()))
         : ZERO_VECTOR_3d;
   }

   static float getBlindness() {
      Entity cameraEntity = client.getCameraEntity();
      if (cameraEntity instanceof LivingEntity) {
         MobEffectInstance blindness = ((LivingEntity)cameraEntity).getEffect(MobEffects.BLINDNESS);
         if (blindness != null) {
            if (blindness.isInfiniteDuration()) {
               return 1.0F;
            }

            return Math.clamp(0.0F, 1.0F, blindness.getDuration() / 20.0F);
         }
      }

      return 0.0F;
   }

   static float getDarknessFactor() {
      Entity cameraEntity = client.getCameraEntity();
      if (cameraEntity instanceof LivingEntity) {
         MobEffectInstance darkness = ((LivingEntity)cameraEntity).getEffect(MobEffects.DARKNESS);
         if (darkness != null) {
            return darkness.getBlendFactor((LivingEntity)cameraEntity, CapturedRenderingState.INSTANCE.getTickDelta());
         }
      }

      return 0.0F;
   }

   private static float getPlayerMood() {
      return !(client.cameraEntity instanceof LocalPlayer) ? 0.0F : Math.clamp(0.0F, 1.0F, ((LocalPlayer)client.cameraEntity).getCurrentMood());
   }

   private static float getConstantMood() {
      return !(client.cameraEntity instanceof LocalPlayer)
         ? 0.0F
         : Math.clamp(0.0F, 1.0F, ((LocalPlayerInterface)client.cameraEntity).getCurrentConstantMood());
   }

   static float getRainStrength() {
      return client.level == null ? 0.0F : Math.clamp(0.0F, 1.0F, client.level.getRainLevel(CapturedRenderingState.INSTANCE.getTickDelta()));
   }

   private static Vector2i getEyeBrightness() {
      if (client.cameraEntity != null && client.level != null) {
         Vec3 feet = client.cameraEntity.position();
         Vec3 eyes = new Vec3(feet.x, client.cameraEntity.getEyeY(), feet.z);
         BlockPos eyeBlockPos = BlockPos.containing(eyes);
         int blockLight = client.level.getBrightness(LightLayer.BLOCK, eyeBlockPos);
         int skyLight = client.level.getBrightness(LightLayer.SKY, eyeBlockPos);
         return new Vector2i(blockLight * 16, skyLight * 16);
      } else {
         return ZERO_VECTOR_2i;
      }
   }

   private static float getNightVision() {
      if (client.getCameraEntity() instanceof LivingEntity livingEntity) {
         try {
            float nightVisionStrength = GameRenderer.getNightVisionScale(livingEntity, CapturedRenderingState.INSTANCE.getTickDelta());
            if (nightVisionStrength > 0.0F) {
               return Math.clamp(0.0F, 1.0F, nightVisionStrength);
            }
         } catch (NullPointerException var3) {
            return 0.0F;
         }
      }

      if (client.player != null && client.player.hasEffect(MobEffects.CONDUIT_POWER)) {
         float underwaterVisibility = client.player.getWaterVision();
         if (underwaterVisibility > 0.0F) {
            return Math.clamp(0.0F, 1.0F, underwaterVisibility);
         }
      }

      return 0.0F;
   }

   static int isEyeInWater() {
      FogType submersionType = client.gameRenderer.getMainCamera().getFluidInCamera();
      if (submersionType == FogType.WATER) {
         return 1;
      } else if (submersionType == FogType.LAVA) {
         return 2;
      } else {
         return submersionType == FogType.POWDER_SNOW ? 3 : 0;
      }
   }

   static {
      GbufferPrograms.init();
   }
}
