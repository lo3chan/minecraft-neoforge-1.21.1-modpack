package dev.tr7zw.notenoughanimations.animations.hands;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import dev.tr7zw.notenoughanimations.versionless.animations.HoldUpModes;
import dev.tr7zw.transition.mc.EntityUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LookAtItemAnimation extends BasicAnimation {
   private Set<Item> holdingItems = new HashSet<>();
   private final BodyPart[] bothHands = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM};
   private final BodyPart[] left = new BodyPart[]{BodyPart.LEFT_ARM};
   private final BodyPart[] right = new BodyPart[]{BodyPart.RIGHT_ARM};
   private BodyPart[] target = this.bothHands;

   @Override
   public boolean isEnabled() {
      this.bind();
      return NEABaseMod.config.holdUpItemsMode != HoldUpModes.NONE && !this.holdingItems.isEmpty();
   }

   private void bind() {
      this.holdingItems.clear();
      this.holdingItems.addAll(AnimationUtil.parseItemList(NEABaseMod.config.holdingItems));
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      if (NEABaseMod.config.holdUpOnlySelf && entity != Minecraft.getInstance().player) {
         return false;
      } else {
         boolean allItems = NEABaseMod.config.holdUpItemsMode == HoldUpModes.ALL;
         ItemStack itemInRightHand = entity.getItemInHand(entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
         ItemStack itemInLeftHand = entity.getItemInHand(entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
         boolean rightArm = NEABaseMod.config.holdUpItemsMode == HoldUpModes.CONFIG && this.holdingItems.contains(itemInRightHand.getItem())
            || NEABaseMod.config.holdUpItemsMode == HoldUpModes.CONFIG_INVERTED && !this.holdingItems.contains(itemInRightHand.getItem())
            || allItems && !itemInRightHand.isEmpty() && (!entity.swinging || entity.getMainArm() != HumanoidArm.RIGHT);
         boolean leftArm = NEABaseMod.config.holdUpItemsMode == HoldUpModes.CONFIG && this.holdingItems.contains(itemInLeftHand.getItem())
            || NEABaseMod.config.holdUpItemsMode == HoldUpModes.CONFIG_INVERTED && !this.holdingItems.contains(itemInLeftHand.getItem())
            || allItems && !itemInLeftHand.isEmpty() && (!entity.swinging || entity.getMainArm() != HumanoidArm.LEFT);
         if (rightArm && leftArm && !entity.swinging) {
            this.target = this.bothHands;
            return true;
         } else if (!rightArm
            || entity.swinging && entity.swingingArm == (entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)) {
            if (!leftArm
               || entity.swinging && entity.swingingArm == (entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)) {
               return false;
            } else {
               this.target = this.left;
               return true;
            }
         } else {
            this.target = this.right;
            return true;
         }
      }
   }

   @Override
   public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
      return this.target;
   }

   @Override
   public int getPriority(AbstractClientPlayer entity, PlayerData data) {
      return 300;
   }

   @Override
   public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel model, BodyPart part, float delta, float tickCounter) {
      HumanoidArm arm = part == BodyPart.LEFT_ARM ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
      switch (NEABaseMod.config.holdUpTarget) {
         case NONE:
            AnimationUtil.applyArmTransforms(
               model, arm, -NEABaseMod.config.holdUpItemOffset - Mth.lerp(-1.0F * (EntityUtil.getXRot(entity) - 90.0F) / 180.0F, 1.0F, 1.5F), -0.2F, 0.3F
            );
            break;
         case CAMERA:
            float invert = part == BodyPart.LEFT_ARM ? -1.0F : 1.0F;
            AnimationUtil.applyArmTransforms(
               model,
               arm,
               Mth.clamp(-1.5707964F + model.head.xRot, -2.5F, 0.0F),
               Mth.clamp(NEABaseMod.config.holdUpCameraOffset + model.head.yRot * invert, -0.2F, Math.max(0.2F, NEABaseMod.config.holdUpCameraOffset)),
               0.1F
            );
      }
   }
}
