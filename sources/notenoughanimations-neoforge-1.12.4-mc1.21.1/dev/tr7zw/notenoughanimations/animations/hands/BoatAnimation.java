package dev.tr7zw.notenoughanimations.animations.hands;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatAnimation extends BasicAnimation {
   private final BodyPart[] bothHands = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM};

   @Override
   public boolean isEnabled() {
      return NEABaseMod.config.enableRowBoatAnimation;
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      return entity.isPassenger() && entity.getVehicle() instanceof Boat;
   }

   @Override
   public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
      return this.bothHands;
   }

   @Override
   public int getPriority(AbstractClientPlayer entity, PlayerData data) {
      return 1500;
   }

   @Override
   public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel model, BodyPart part, float delta, float tickCounter) {
      if (part != BodyPart.BODY) {
         if (part != BodyPart.LEFT_ARM || !AnimationUtil.isSwingingArm(entity, part)) {
            if (part != BodyPart.RIGHT_ARM || !AnimationUtil.isSwingingArm(entity, part)) {
               HumanoidArm arm = part == BodyPart.LEFT_ARM ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
               Boat boat = (Boat)entity.getVehicle();
               int id = boat.getPassengers().indexOf(entity);
               if (id == 0) {
                  float paddle = boat.getRowingTime(arm == HumanoidArm.LEFT ? 0 : 1, delta);
                  AnimationUtil.applyArmTransforms(model, arm, -1.1F - Mth.sin(paddle) * 0.3F, 0.2F, 0.3F);
               }
            }
         }
      }
   }
}
