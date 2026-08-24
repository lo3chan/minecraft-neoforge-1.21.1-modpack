package dev.tr7zw.notenoughanimations.animations.vanilla;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import java.util.EnumSet;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class VanillaSingleHandedAnimation extends BasicAnimation {
   private ArmPose rightArmPose;
   private ArmPose leftArmPose;
   private final EnumSet<ArmPose> singleHandedAnimatios = EnumSet.of(ArmPose.SPYGLASS, ArmPose.THROW_SPEAR);
   private final BodyPart[] left = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.BODY};
   private final BodyPart[] right = new BodyPart[]{BodyPart.RIGHT_ARM, BodyPart.BODY};

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      this.rightArmPose = AnimationUtil.getArmPose(entity, entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
      this.leftArmPose = AnimationUtil.getArmPose(entity, entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
      return this.singleHandedAnimatios.contains(this.leftArmPose) || this.singleHandedAnimatios.contains(this.rightArmPose);
   }

   @Override
   public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
      if (this.singleHandedAnimatios.contains(this.leftArmPose)) {
         return this.left;
      } else {
         return this.singleHandedAnimatios.contains(this.rightArmPose) ? this.right : new BodyPart[0];
      }
   }

   @Override
   public int getPriority(AbstractClientPlayer entity, PlayerData data) {
      return 3100;
   }

   @Override
   public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel model, BodyPart part, float delta, float tickCounter) {
   }
}
