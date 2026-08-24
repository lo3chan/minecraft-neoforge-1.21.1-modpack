package net.bettercombat.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier.PartModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation.AnimationBuilder;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import java.util.List;
import java.util.Optional;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.animation.AttackAnimationSubStack;
import net.bettercombat.client.animation.CustomAnimationPlayer;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.animation.PoseSubStack;
import net.bettercombat.client.animation.StateCollectionHelper;
import net.bettercombat.client.animation.modifier.HarshAdjustmentModifier;
import net.bettercombat.client.animation.modifier.TransmissionSpeedModifier;
import net.bettercombat.client.compat.FirstPersonAnimationCompatibility;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.Pose;
import net.bettercombat.mixin.player.LivingEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractClientPlayer.class})
public abstract class AbstractClientPlayerEntityMixin extends Player implements PlayerAttackAnimatable {
   private final AttackAnimationSubStack attackAnimation = new AttackAnimationSubStack(this.createAttackAdjustment());
   private final PoseSubStack mainHandBodyPose = new PoseSubStack(this.createPoseAdjustment(), true, true);
   private final PoseSubStack mainHandItemPose = new PoseSubStack(null, false, true);
   private final PoseSubStack offHandBodyPose = new PoseSubStack(null, true, false);
   private final PoseSubStack offHandItemPose = new PoseSubStack(null, false, true);
   @Nullable
   private SlashParticleUtil.ScheduledSpawnArgs scheduledParticles = null;

   public AbstractClientPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
      super(world, pos, yaw, gameProfile);
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lcom/mojang/authlib/GameProfile;)V"},
      at = {@At("TAIL")}
   )
   private void postInit(ClientLevel world, GameProfile profile, CallbackInfo ci) {
      AnimationStack stack = ((IAnimatedPlayer)this).getAnimationStack();
      stack.addAnimLayer(1, this.offHandItemPose.base);
      stack.addAnimLayer(2, this.offHandBodyPose.base);
      stack.addAnimLayer(3, this.mainHandItemPose.base);
      stack.addAnimLayer(4, this.mainHandBodyPose.base);
      stack.addAnimLayer(2000, this.attackAnimation.base);
      this.mainHandBodyPose.configure = this::updateAnimationByCurrentActivity;
      this.offHandBodyPose.configure = this::updateAnimationByCurrentActivity;
   }

   @Override
   public void updateAnimationsOnTick() {
      Player player = this;
      boolean isLeftHanded = this.isLeftHanded();
      boolean hasActiveAttackAnimation = this.attackAnimation.base.getAnimation() != null && this.attackAnimation.base.getAnimation().isActive();
      ItemStack mainHandStack = player.getMainHandItem();
      if (this.scheduledParticles != null && this.scheduledParticles.time() == player.tickCount) {
         SlashParticleUtil.spawnParticles(this.scheduledParticles.args());
         this.scheduledParticles = null;
      }

      if (!player.swinging
         && !player.isSwimming()
         && !player.isUsingItem()
         && !player.onClimbable()
         && !player.isFallFlying()
         && !Platform.isCastingSpell(player)
         && !CrossbowItem.isCharged(mainHandStack)) {
         if (hasActiveAttackAnimation) {
            ((LivingEntityAccessor)player).invokeTurnHead(player.getYHeadRot(), 0.0F);
         }

         EntityPlayer_BetterCombat betterCombatPlayer = (EntityPlayer_BetterCombat)player;
         KeyframeAnimation newMainHandPose = null;
         KeyframeAnimation newOffHandPose = null;
         if (Minecraft.getInstance().player == player) {
            Pose pose = PlayerAttackHelper.poseForPlayer(player);
            if (!pose.base().isEmpty()) {
               newMainHandPose = (KeyframeAnimation)PlayerAnimationRegistry.getAnimation(ResourceLocation.parse(pose.base()));
            }

            if (!pose.offHand().isEmpty()) {
               newOffHandPose = (KeyframeAnimation)PlayerAnimationRegistry.getAnimation(ResourceLocation.parse(pose.offHand()));
            }
         } else {
            if (betterCombatPlayer.getMainHandIdleAnimation() != null && !betterCombatPlayer.getMainHandIdleAnimation().isEmpty()) {
               newMainHandPose = (KeyframeAnimation)PlayerAnimationRegistry.getAnimation(ResourceLocation.parse(betterCombatPlayer.getMainHandIdleAnimation()));
            }

            if (betterCombatPlayer.getOffHandIdleAnimation() != null && !betterCombatPlayer.getOffHandIdleAnimation().isEmpty()) {
               newOffHandPose = (KeyframeAnimation)PlayerAnimationRegistry.getAnimation(ResourceLocation.parse(betterCombatPlayer.getOffHandIdleAnimation()));
            }
         }

         this.mainHandItemPose.setPose(newMainHandPose, isLeftHanded);
         this.offHandItemPose.setPose(newOffHandPose, isLeftHanded);
         if (!PlayerAttackHelper.isTwoHandedWielding(player) && (this.isWalking() || this.isShiftKeyDown())) {
            newMainHandPose = null;
            newOffHandPose = null;
         }

         this.mainHandBodyPose.setPose(newMainHandPose, isLeftHanded);
         this.offHandBodyPose.setPose(newOffHandPose, isLeftHanded);
      } else {
         this.mainHandBodyPose.setPose(null, isLeftHanded);
         this.mainHandItemPose.setPose(null, isLeftHanded);
         this.offHandBodyPose.setPose(null, isLeftHanded);
         this.offHandItemPose.setPose(null, isLeftHanded);
      }
   }

   @Override
   public void playAttackAnimation(String name, AnimatedHand animatedHand, float length, float upswing) {
      try {
         KeyframeAnimation animation = (KeyframeAnimation)PlayerAnimationRegistry.getAnimation(ResourceLocation.parse(name));
         AnimationBuilder copy = animation.mutableCopy();
         this.updateAnimationByCurrentActivity(copy);
         copy.torso.fullyEnablePart(true);
         copy.head.pitch.setEnabled(false);
         float speed = animation.endTick / length;
         boolean mirror = animatedHand.isOffHand();
         if (this.isLeftHanded()) {
            mirror = !mirror;
         }

         int fadeIn = copy.beginTick;
         float trueUpswingRatio = upswing / BetterCombatMod.config.getUpswingMultiplier();
         float upswingSpeed = speed / trueUpswingRatio;
         float downwindSpeed = (float)(
            speed * Mth.lerp(Math.max(BetterCombatMod.config.getUpswingMultiplier() - 0.5, 0.0) / 0.5, 1.0F - upswing, upswing / (1.0F - upswing))
         );
         this.attackAnimation
            .speed
            .set(upswingSpeed, List.of(new TransmissionSpeedModifier.Gear(length * upswing, downwindSpeed), new TransmissionSpeedModifier.Gear(length, speed)));
         this.attackAnimation.mirror.setEnabled(mirror);
         CustomAnimationPlayer player = new CustomAnimationPlayer(copy.build(), 0);
         player.setFirstPersonMode(FirstPersonAnimationCompatibility.firstPersonMode());
         player.setFirstPersonConfiguration(this.firstPersonConfig(animatedHand));
         this.attackAnimation.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeIn, Ease.INOUTSINE), player);
      } catch (Exception var14) {
         var14.printStackTrace();
      }
   }

   @Override
   public void playAttackParticles(boolean isOffHand, float weaponRange, int delay, List<ParticlePlacement> particles, TrailAppearance appearance) {
      AbstractClientPlayer player = (AbstractClientPlayer)this;
      SlashParticleUtil.SpawnArgs spawn = new SlashParticleUtil.SpawnArgs(player, isOffHand, weaponRange, particles, appearance);
      this.scheduledParticles = new SlashParticleUtil.ScheduledSpawnArgs(spawn, player.tickCount + delay);
   }

   private AdjustmentModifier createAttackAdjustment() {
      return new AdjustmentModifier(partName -> {
         float rotationX = 0.0F;
         float rotationY = 0.0F;
         float rotationZ = 0.0F;
         float offsetX = 0.0F;
         float offsetY = 0.0F;
         float offsetZ = 0.0F;
         if (FirstPersonMode.isFirstPersonPass()) {
            float pitch = this.getXRot();
            pitch = (float)Math.toRadians(pitch);
            byte var10 = -1;
            switch (partName.hashCode()) {
               case 3029410:
                  if (partName.equals("body")) {
                     var10 = 0;
                  }
               default:
                  switch (var10) {
                     case 0:
                        rotationX -= pitch;
                        if (pitch < 0.0F) {
                           double offset = Math.abs(Math.sin(pitch));
                           offsetY = (float)(offsetY + offset * 0.5);
                           offsetZ = (float)(offsetZ - offset);
                        }
                        break;
                     default:
                        return Optional.empty();
                  }
            }
         } else {
            float pitch = this.getXRot();
            pitch = (float)Math.toRadians(pitch);
            switch (partName) {
               case "body":
                  rotationX -= pitch * 0.75F;
                  break;
               case "rightArm":
               case "leftArm":
                  rotationX += pitch * 0.25F;
                  break;
               case "rightLeg":
               case "leftLeg":
                  rotationX = (float)(rotationX - pitch * 0.75);
                  break;
               default:
                  return Optional.empty();
            }
         }

         return Optional.of(new PartModifier(new Vec3f(rotationX, rotationY, rotationZ), new Vec3f(offsetX, offsetY, offsetZ)));
      });
   }

   private AdjustmentModifier createPoseAdjustment() {
      return new HarshAdjustmentModifier(partName -> {
         float rotationX = 0.0F;
         float rotationY = 0.0F;
         float rotationZ = 0.0F;
         float offsetX = 0.0F;
         float offsetY = 0.0F;
         float offsetZ = 0.0F;
         if (!FirstPersonMode.isFirstPersonPass()) {
            switch (partName) {
               case "rightArm":
               case "leftArm":
                  if (!this.mainHandItemPose.lastAnimationUsesBodyChannel && this.isCrouching()) {
                     offsetY += 3.0F;
                  }
                  break;
               default:
                  return Optional.empty();
            }
         }

         return Optional.of(new PartModifier(new Vec3f(rotationX, rotationY, rotationZ), new Vec3f(offsetX, offsetY, offsetZ)));
      });
   }

   private void updateAnimationByCurrentActivity(AnimationBuilder animation) {
      net.minecraft.world.entity.Pose pose = this.getPose();
      switch (pose) {
         case STANDING:
         case FALL_FLYING:
         case SLEEPING:
         case SPIN_ATTACK:
         case CROUCHING:
         case LONG_JUMPING:
         case DYING:
         default:
            break;
         case SWIMMING:
            StateCollectionHelper.configure(animation.rightLeg, false, false);
            StateCollectionHelper.configure(animation.leftLeg, false, false);
      }

      if (this.isMounting()) {
         StateCollectionHelper.configure(animation.rightLeg, false, false);
         StateCollectionHelper.configure(animation.leftLeg, false, false);
      } else {
         float legAnimationThreshold = BetterCombatClientMod.config.legAnimationThreshold;
         if (BetterCombatClientMod.config.legAnimationThreshold > 0.0F) {
            boolean moving = this.isSprinting() || this.isWalking();
            if (moving && this.getDeltaMovement().horizontalDistanceSqr() > legAnimationThreshold * legAnimationThreshold) {
               StateCollectionHelper.configure(animation.rightLeg, false, false);
               StateCollectionHelper.configure(animation.leftLeg, false, false);
            }
         }
      }
   }

   private boolean isWalking() {
      return !this.isDeadOrDying() && (this.isSwimming() || this.getDeltaMovement().horizontalDistance() > 0.03);
   }

   private boolean isMounting() {
      return this.getVehicle() != null;
   }

   public boolean isLeftHanded() {
      return this.getMainArm() == HumanoidArm.LEFT;
   }

   @Override
   public void stopAttackAnimation(float length) {
      IAnimation currentAnimation = this.attackAnimation.base.getAnimation();
      this.scheduledParticles = null;
      if (currentAnimation != null && currentAnimation instanceof KeyframeAnimationPlayer) {
         int fadeOut = Math.round(length);
         this.attackAnimation.adjustmentModifier.fadeOut(fadeOut);
         this.attackAnimation.base.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeOut, Ease.INOUTSINE), null);
      }
   }

   private FirstPersonConfiguration firstPersonConfig(AnimatedHand animatedHand) {
      boolean showRightItem = true;
      boolean showLeftItem = BetterCombatClientMod.config.isShowingOtherHandFirstPerson || animatedHand == AnimatedHand.TWO_HANDED;
      boolean showRightArm = showRightItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;
      boolean showLeftArm = showLeftItem && BetterCombatClientMod.config.isShowingArmsInFirstPerson;
      return new FirstPersonConfiguration(showRightArm, showLeftArm, showRightItem, showLeftItem);
   }
}
