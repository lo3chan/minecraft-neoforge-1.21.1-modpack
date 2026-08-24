package net.irisshaders.iris.uniforms;

import java.util.Objects;
import java.util.stream.StreamSupport;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.mixin.GameRendererAccessor;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class IrisExclusiveUniforms {
   private static final Vector3d ZERO = new Vector3d(0.0);

   public static void addIrisExclusiveUniforms(UniformHolder uniforms) {
      IrisExclusiveUniforms.WorldInfoUniforms.addWorldInfoUniforms(uniforms);
      uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "currentColorSpace", () -> IrisVideoSettings.colorSpace.ordinal());
      uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "thunderStrength", IrisExclusiveUniforms::getThunderStrength);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerHealth", IrisExclusiveUniforms::getCurrentHealth);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerHealth", IrisExclusiveUniforms::getMaxHealth);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerHunger", IrisExclusiveUniforms::getCurrentHunger);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerHunger", () -> 20);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerArmor", IrisExclusiveUniforms::getCurrentArmor);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerArmor", () -> 50);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "currentPlayerAir", IrisExclusiveUniforms::getCurrentAir);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "maxPlayerAir", IrisExclusiveUniforms::getMaxAir);
      uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "firstPersonCamera", IrisExclusiveUniforms::isFirstPersonCamera);
      uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "isSpectator", IrisExclusiveUniforms::isSpectator);
      uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "currentSelectedBlockId", IrisExclusiveUniforms::getCurrentSelectedBlockId);
      uniforms.uniform3f(UniformUpdateFrequency.PER_FRAME, "currentSelectedBlockPos", IrisExclusiveUniforms::getCurrentSelectedBlockPos);
      uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "eyePosition", IrisExclusiveUniforms::getEyePosition);
      uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "cloudTime", CapturedRenderingState.INSTANCE::getCloudTime);
      uniforms.uniform3d(UniformUpdateFrequency.PER_FRAME, "relativeEyePosition", () -> CameraUniforms.getUnshiftedCameraPosition().sub(getEyePosition()));
      uniforms.uniform3d(
         UniformUpdateFrequency.PER_FRAME,
         "playerLookVector",
         () -> Minecraft.getInstance().cameraEntity instanceof LivingEntity livingEntity
            ? JomlConversions.fromVec3(livingEntity.getViewVector(CapturedRenderingState.INSTANCE.getTickDelta()))
            : ZERO
      );
      uniforms.uniform3d(
         UniformUpdateFrequency.PER_FRAME, "playerBodyVector", () -> JomlConversions.fromVec3(Minecraft.getInstance().getCameraEntity().getForward())
      );
      Vector4f zero = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
      uniforms.uniform4f(
         UniformUpdateFrequency.PER_TICK,
         "lightningBoltPosition",
         () -> Minecraft.getInstance().level != null
            ? StreamSupport.<Entity>stream(Minecraft.getInstance().level.entitiesForRendering().spliterator(), false)
               .filter(bolt -> bolt instanceof LightningBolt)
               .findAny()
               .map(
                  bolt -> {
                     Vector3d unshiftedCameraPosition = CameraUniforms.getUnshiftedCameraPosition();
                     Vec3 vec3 = bolt.getPosition(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
                     return new Vector4f(
                        (float)(vec3.x - unshiftedCameraPosition.x),
                        (float)(vec3.y - unshiftedCameraPosition.y),
                        (float)(vec3.z - unshiftedCameraPosition.z),
                        1.0F
                     );
                  }
               )
               .orElse(zero)
            : zero
      );
   }

   private static int getCurrentSelectedBlockId() {
      HitResult hitResult = Minecraft.getInstance().hitResult;
      if (Minecraft.getInstance().level != null
         && ((GameRendererAccessor)Minecraft.getInstance().gameRenderer).shouldRenderBlockOutlineA()
         && hitResult != null
         && hitResult.getType() == Type.BLOCK) {
         BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
         BlockState blockState = Minecraft.getInstance().level.getBlockState(blockPos4);
         if (!blockState.isAir() && Minecraft.getInstance().level.getWorldBorder().isWithinBounds(blockPos4)) {
            return WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(blockState);
         }
      }

      return 0;
   }

   private static Vector3f getCurrentSelectedBlockPos() {
      HitResult hitResult = Minecraft.getInstance().hitResult;
      if (Minecraft.getInstance().level != null
         && ((GameRendererAccessor)Minecraft.getInstance().gameRenderer).shouldRenderBlockOutlineA()
         && hitResult != null
         && hitResult.getType() == Type.BLOCK) {
         BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
         return blockPos4.getCenter().subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).toVector3f();
      } else {
         return new Vector3f(-256.0F);
      }
   }

   private static float getThunderStrength() {
      return Math.clamp(0.0F, 1.0F, Minecraft.getInstance().level.getThunderLevel(CapturedRenderingState.INSTANCE.getTickDelta()));
   }

   private static float getCurrentHealth() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? Minecraft.getInstance().player.getHealth() / Minecraft.getInstance().player.getMaxHealth()
         : -1.0F;
   }

   private static float getCurrentHunger() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? Minecraft.getInstance().player.getFoodData().getFoodLevel() / 20.0F
         : -1.0F;
   }

   private static float getCurrentAir() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? (float)Minecraft.getInstance().player.getAirSupply() / Minecraft.getInstance().player.getMaxAirSupply()
         : -1.0F;
   }

   private static float getCurrentArmor() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? Minecraft.getInstance().player.getArmorValue() / 50.0F
         : -1.0F;
   }

   private static float getMaxAir() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? Minecraft.getInstance().player.getMaxAirSupply()
         : -1.0F;
   }

   private static float getMaxHealth() {
      return Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode.getPlayerMode().isSurvival()
         ? Minecraft.getInstance().player.getMaxHealth()
         : -1.0F;
   }

   private static boolean isFirstPersonCamera() {
      return switch (Minecraft.getInstance().options.getCameraType()) {
         case THIRD_PERSON_BACK, THIRD_PERSON_FRONT -> false;
         default -> true;
      };
   }

   private static boolean isSpectator() {
      return Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR;
   }

   private static Vector3d getEyePosition() {
      Objects.requireNonNull(Minecraft.getInstance().getCameraEntity());
      Vec3 pos = Minecraft.getInstance().getCameraEntity().getEyePosition(CapturedRenderingState.INSTANCE.getTickDelta());
      return new Vector3d(pos.x, pos.y, pos.z);
   }

   public static class WorldInfoUniforms {
      public static void addWorldInfoUniforms(UniformHolder uniforms) {
         ClientLevel level = Minecraft.getInstance().level;
         uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "bedrockLevel", () -> level != null ? level.dimensionType().minY() : 0);
         uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "cloudHeight", () -> level != null ? level.effects().getCloudHeight() : 192.0);
         uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "heightLimit", () -> level != null ? level.dimensionType().height() : 256);
         uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, "logicalHeightLimit", () -> level != null ? level.dimensionType().logicalHeight() : 256);
         uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hasCeiling", () -> level != null ? level.dimensionType().hasCeiling() : false);
         uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "hasSkylight", () -> level != null ? level.dimensionType().hasSkyLight() : true);
         uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "ambientLight", () -> level != null ? level.dimensionType().ambientLight() : 0.0F);
      }
   }
}
