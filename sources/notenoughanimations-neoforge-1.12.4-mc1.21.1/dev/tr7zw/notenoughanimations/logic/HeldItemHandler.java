package dev.tr7zw.notenoughanimations.logic;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.util.MapRenderer;
import dev.tr7zw.notenoughanimations.util.NMSWrapper;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.DataHolder;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.ItemUtil;
import dev.tr7zw.transition.mc.MathUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HeldItemHandler implements DataHolder<HeldItemHandler.HeldItemState> {
   private Item filledMap = ItemUtil.getItem(GeneralUtil.getResourceLocation("minecraft", "filled_map"));
   private Set<Item> hideItemsForTheseBows = new HashSet<>();
   private Set<Item> lanternItems = new HashSet<>();

   public void onLoad() {
      this.hideItemsForTheseBows.clear();
      this.hideItemsForTheseBows.addAll(AnimationUtil.parseItemList(NEABaseMod.config.hideItemsForTheseBows));
      this.lanternItems.clear();
      this.lanternItems.addAll(AnimationUtil.parseItemList(NEABaseMod.config.lanternItems));
   }

   public void onRenderItem(
      LivingEntity entity,
      EntityModel<?> model,
      ItemStack itemStack,
      HumanoidArm arm,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      CallbackInfo info
   ) {
      if (entity != null) {
         if (entity.isSleeping()) {
            if (NEABaseMod.config.dontHoldItemsInBed) {
               info.cancel();
            }
         } else if (!NMSWrapper.hasCustomModel(itemStack)) {
            if (model instanceof ArmedModel armedModel
               && model instanceof HumanoidModel<?> humanoid
               && (arm == HumanoidArm.RIGHT && humanoid.rightArm.visible || arm == HumanoidArm.LEFT && humanoid.leftArm.visible)
               && NEABaseMod.config.enableInWorldMapRendering) {
               if (arm == entity.getMainArm() && entity.getMainHandItem().getItem().equals(this.filledMap)) {
                  matrices.pushPose();
                  armedModel.translateToHand(arm, matrices);
                  matrices.mulPose(MathUtil.XP.rotationDegrees(-90.0F));
                  matrices.mulPose(MathUtil.YP.rotationDegrees(205.0F));
                  matrices.mulPose(MathUtil.ZP.rotationDegrees(10.0F));
                  boolean bl = arm == HumanoidArm.LEFT;
                  matrices.translate((bl ? -1 : 1) / 16.0F, 0.09 + (entity.getOffhandItem().isEmpty() ? 0.15 : 0.0), -0.625);
                  MapRenderer.renderFirstPersonMap(
                     matrices, vertexConsumers, light, itemStack, !entity.getOffhandItem().isEmpty(), entity.getMainArm() == HumanoidArm.LEFT
                  );
                  matrices.popPose();
                  info.cancel();
                  return;
               }

               if (arm != entity.getMainArm() && entity.getOffhandItem().getItem().equals(this.filledMap)) {
                  matrices.pushPose();
                  armedModel.translateToHand(arm, matrices);
                  matrices.mulPose(MathUtil.XP.rotationDegrees(-90.0F));
                  matrices.mulPose(MathUtil.YP.rotationDegrees(200.0F));
                  boolean bl = arm == HumanoidArm.LEFT;
                  matrices.translate((bl ? -1 : 1) / 16.0F, 0.125, -0.625);
                  MapRenderer.renderFirstPersonMap(matrices, vertexConsumers, light, itemStack, true, false);
                  matrices.popPose();
                  info.cancel();
                  return;
               }
            }

            if (NEABaseMod.config.enableOffhandHiding
               && entity instanceof AbstractClientPlayer player
               && !(player.getMainHandItem().getItem() instanceof ShieldItem)) {
               boolean mainHandProjectileWeapon = player.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
               boolean offHandProjectileWeapon = player.getOffhandItem().getItem() instanceof ProjectileWeaponItem;
               if (!mainHandProjectileWeapon) {
                  mainHandProjectileWeapon = this.hideItemsForTheseBows.contains(player.getMainHandItem().getItem());
               }

               if (!offHandProjectileWeapon) {
                  offHandProjectileWeapon = this.hideItemsForTheseBows.contains(player.getOffhandItem().getItem());
               }

               boolean projectileWeaponEquipped = mainHandProjectileWeapon || offHandProjectileWeapon;
               boolean mainHandCharged = AnimationUtil.isChargedCrossbow(player.getMainHandItem());
               boolean offHandCharged = AnimationUtil.isChargedCrossbow(player.getOffhandItem());
               boolean isUsingItem = player.isUsingItem();
               if (!mainHandCharged && isUsingItem) {
                  mainHandCharged = (float)(player.getMainHandItem().getUseDuration(player) - player.getUseItemRemainingTicks())
                        / CrossbowItem.getChargeDuration(player.getMainHandItem(), player)
                     >= 1.0F;
               }

               if (!offHandCharged && isUsingItem) {
                  offHandCharged = (float)(player.getOffhandItem().getUseDuration(player) - player.getUseItemRemainingTicks())
                        / CrossbowItem.getChargeDuration(player.getOffhandItem(), player)
                     >= 1.0F;
               }

               ArmPose mainHandPose = AnimationUtil.getArmPose(player, InteractionHand.MAIN_HAND);
               ArmPose offHandPose = AnimationUtil.getArmPose(player, InteractionHand.OFF_HAND);
               if (!AnimationUtil.isUsingBothHands(mainHandPose)
                  && !AnimationUtil.isUsingBothHands(offHandPose)
                  && (!projectileWeaponEquipped || !mainHandCharged && !offHandCharged && !isUsingItem)) {
                  return;
               }

               if (mainHandPose.isTwoHanded()) {
                  offHandPose = player.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
               }

               HumanoidArm mainArm = HumanoidArm.RIGHT;
               HumanoidArm offArm = HumanoidArm.LEFT;
               if (player.getMainArm() == HumanoidArm.LEFT) {
                  mainArm = HumanoidArm.LEFT;
                  offArm = HumanoidArm.RIGHT;
               }

               if (arm == mainArm && AnimationUtil.isUsingBothHands(offHandPose)) {
                  info.cancel();
                  return;
               }

               if (arm == offArm && AnimationUtil.isUsingBothHands(mainHandPose)) {
                  info.cancel();
                  return;
               }

               if (projectileWeaponEquipped
                  && (mainHandCharged || offHandCharged || isUsingItem)
                  && (!mainHandCharged && !offHandCharged || !isUsingItem)
                  && !AnimationUtil.isUsingBothHands(mainHandPose)
                  && !AnimationUtil.isUsingBothHands(offHandPose)) {
                  if (arm == mainArm && offHandProjectileWeapon && !mainHandProjectileWeapon) {
                     info.cancel();
                     return;
                  }

                  if (arm == offArm && mainHandProjectileWeapon) {
                     info.cancel();
                     return;
                  }
               }
            }
         }
      }
   }

   public static class HeldItemState {
      public int lanternLastTick = 0;
      public Vec3 lanternPos;
      public Vec3 lanternVelocity = Vec3.ZERO;
      public Vec3 lastLanternVelocity = Vec3.ZERO;
      public Vec3 smoothedHandPos = null;

      public HeldItemState(LivingEntity entity) {
         this.lanternLastTick = entity.tickCount;
         this.lanternPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
      }
   }
}
