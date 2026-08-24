package dev.tr7zw.notenoughanimations.animations.fullbody;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class CrawlingAnimation extends BasicAnimation {
   private BodyPart[] bodyParts = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG};
   private final float speedMul = 2.5F;
   private float swimAmount;
   private float attackTime;
   private float animationStep;
   private float animationStep2;
   private HumanoidArm humanoidArm;
   private float m;
   private float n;
   private float armMoveHight = 0.3707964F;
   private final float legPitch = 0.15F;
   private final float r = 0.33333334F;

   @Override
   public boolean isEnabled() {
      return NEABaseMod.config.enableCrawlingAnimation;
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      return entity.getPose() == Pose.SWIMMING && !entity.isInWater();
   }

   @Override
   public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
      return this.bodyParts;
   }

   @Override
   public int getPriority(AbstractClientPlayer entity, PlayerData data) {
      return 350;
   }

   @Override
   protected void precalculate(AbstractClientPlayer entity, PlayerData data, PlayerModel model, float delta, float swing) {
      this.swimAmount = model.swimAmount;
      this.attackTime = model.attackTime;
      if (this.swimAmount > 0.0F) {
         this.animationStep = swing * 2.5F % 26.0F;
         this.animationStep2 = this.animationStep + 13.0F;
         this.animationStep2 %= 26.0F;
         this.humanoidArm = this.getAttackArm(entity);
         this.m = this.humanoidArm == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         this.n = this.humanoidArm == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
      }
   }

   @Override
   public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel model, BodyPart part, float delta, float tickCounter) {
      if (this.swimAmount > 0.0F) {
         if (part == BodyPart.RIGHT_ARM) {
            if (this.animationStep < 14.0F) {
               model.rightArm.xRot = Mth.lerp(this.m, model.rightArm.xRot, 0.0F);
               model.rightArm.yRot = Mth.lerp(this.m, model.rightArm.yRot, 3.1415927F);
               model.rightArm.zRot = Mth.lerp(
                  this.m, model.rightArm.zRot, 3.1415927F - 1.8707964F * this.quadraticArmUpdate(this.animationStep) / this.quadraticArmUpdate(14.0F)
               );
            } else if (this.animationStep >= 14.0F && this.animationStep < 24.0F) {
               float o = (this.animationStep - 14.0F) / 10.0F;
               model.rightArm.xRot = Mth.lerp(this.m, model.rightArm.xRot, -this.armMoveHight * o);
               model.rightArm.yRot = Mth.lerp(this.m, model.rightArm.yRot, 3.1415927F);
               model.rightArm.zRot = Mth.lerp(this.m, model.rightArm.zRot, 1.2707963F + 1.8707964F * o);
            } else if (this.animationStep >= 24.0F && this.animationStep < 26.0F) {
               float p = (this.animationStep - 24.0F) / 2.0F;
               model.rightArm.xRot = Mth.lerp(this.m, model.rightArm.xRot, -this.armMoveHight + this.armMoveHight * p);
               model.rightArm.yRot = Mth.lerp(this.m, model.rightArm.yRot, 3.1415927F);
               model.rightArm.zRot = Mth.lerp(this.m, model.rightArm.zRot, 3.1415927F);
            }
         }

         if (part == BodyPart.LEFT_ARM) {
            if (this.animationStep2 < 14.0F) {
               model.leftArm.xRot = this.rotlerpRad(this.n, model.leftArm.xRot, 0.0F);
               model.leftArm.yRot = this.rotlerpRad(this.n, model.leftArm.yRot, 3.1415927F);
               model.leftArm.zRot = this.rotlerpRad(
                  this.n, model.leftArm.zRot, 3.1415927F + 1.8707964F * this.quadraticArmUpdate(this.animationStep2) / this.quadraticArmUpdate(14.0F)
               );
            } else if (this.animationStep2 >= 14.0F && this.animationStep2 < 24.0F) {
               float o = (this.animationStep2 - 14.0F) / 10.0F;
               model.leftArm.xRot = this.rotlerpRad(this.n, model.leftArm.xRot, -this.armMoveHight * o);
               model.leftArm.yRot = this.rotlerpRad(this.n, model.leftArm.yRot, 3.1415927F);
               model.leftArm.zRot = this.rotlerpRad(this.n, model.leftArm.zRot, 5.012389F - 1.8707964F * o);
            } else if (this.animationStep2 >= 24.0F && this.animationStep2 < 26.0F) {
               float p = (this.animationStep2 - 24.0F) / 2.0F;
               model.leftArm.xRot = this.rotlerpRad(this.n, model.leftArm.xRot, -this.armMoveHight + this.armMoveHight * p);
               model.leftArm.yRot = this.rotlerpRad(this.n, model.leftArm.yRot, 3.1415927F);
               model.leftArm.zRot = this.rotlerpRad(this.n, model.leftArm.zRot, 3.1415927F);
            }
         }
      }

      tickCounter *= 2.5F;
      if (part == BodyPart.LEFT_LEG) {
         model.leftLeg.xRot = Mth.lerp(this.swimAmount, model.leftLeg.xRot, 0.15F * Mth.cos(tickCounter * 0.33333334F + 3.1415927F));
         model.leftLeg.zRot = -0.1507964F;
      }

      if (part == BodyPart.RIGHT_LEG) {
         model.rightLeg.xRot = Mth.lerp(this.swimAmount, model.rightLeg.xRot, 0.15F * Mth.cos(tickCounter * 0.33333334F));
         model.rightLeg.zRot = 0.1507964F;
      }
   }

   private float rotlerpRad(float f, float g, float h) {
      float i = (h - g) % 6.2831855F;
      if (i < -3.1415927F) {
         i += 6.2831855F;
      }

      if (i >= 3.1415927F) {
         i -= 6.2831855F;
      }

      return g + f * i;
   }

   private float quadraticArmUpdate(float f) {
      return -65.0F * f + f * f;
   }

   private HumanoidArm getAttackArm(Player livingEntity) {
      HumanoidArm humanoidArm = livingEntity.getMainArm();
      return livingEntity.swingingArm == InteractionHand.MAIN_HAND ? humanoidArm : humanoidArm.getOpposite();
   }
}
