package dev.tr7zw.notenoughanimations.animations.hands;

import dev.tr7zw.notenoughanimations.NEAnimationsMod;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.util.NMSWrapper;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import dev.tr7zw.transition.mc.EntityUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MapHoldingAnimation extends BasicAnimation {
   private Set<Item> compatibleMaps = new HashSet<>();
   private final BodyPart[] bothHands = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM};
   private final BodyPart[] left = new BodyPart[]{BodyPart.LEFT_ARM};
   private final BodyPart[] right = new BodyPart[]{BodyPart.RIGHT_ARM};
   private BodyPart[] target = this.bothHands;

   @Override
   public boolean isEnabled() {
      this.bind();
      return NEABaseMod.config.enableInWorldMapRendering || !this.compatibleMaps.isEmpty();
   }

   private void bind() {
      this.compatibleMaps.clear();
      this.compatibleMaps.addAll(AnimationUtil.parseItemList(NEAnimationsMod.config.mapHolding));
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      ItemStack itemInMainHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
      ItemStack itemInOffHand = entity.getItemInHand(InteractionHand.OFF_HAND);
      if (!this.compatibleMaps.contains(itemInMainHand.getItem()) || !itemInOffHand.isEmpty()) {
         if (!this.compatibleMaps.contains(itemInMainHand.getItem()) || itemInOffHand.isEmpty()) {
            if (!this.compatibleMaps.contains(itemInOffHand.getItem()) || itemInOffHand.isEmpty()) {
               return false;
            } else if (NMSWrapper.hasCustomModel(itemInOffHand)) {
               return false;
            } else {
               this.target = entity.getMainArm() == HumanoidArm.RIGHT ? this.left : this.right;
               return true;
            }
         } else if (NMSWrapper.hasCustomModel(itemInMainHand)) {
            return false;
         } else {
            this.target = entity.getMainArm() == HumanoidArm.RIGHT ? this.right : this.left;
            return true;
         }
      } else if (NMSWrapper.hasCustomModel(itemInMainHand)) {
         return false;
      } else {
         this.target = this.bothHands;
         return true;
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
      if (this.target == this.bothHands) {
         AnimationUtil.applyArmTransforms(
            model,
            arm,
            -Mth.lerp(-1.0F * (EntityUtil.getXRot(entity) - 90.0F) / 180.0F, 0.7F, 0.9F),
            Mth.lerp(-1.0F * (EntityUtil.getXRot(entity) - 90.0F) / 180.0F, -0.3F, -0.2F),
            0.3F
         );
      } else {
         AnimationUtil.applyArmTransforms(model, arm, -Mth.lerp(-1.0F * (EntityUtil.getXRot(entity) - 90.0F) / 180.0F, 0.5F, 1.5F), 0.0F, 0.3F);
      }
   }
}
