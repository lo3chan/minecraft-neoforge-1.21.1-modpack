package dev.tr7zw.notenoughanimations.animations.fullbody;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.api.BasicAnimation;
import dev.tr7zw.notenoughanimations.api.PoseOverwrite;
import dev.tr7zw.notenoughanimations.util.AnimationUtil;
import dev.tr7zw.notenoughanimations.util.NMSWrapper;
import dev.tr7zw.notenoughanimations.versionless.NEABaseMod;
import dev.tr7zw.notenoughanimations.versionless.animations.BodyPart;
import dev.tr7zw.transition.mc.GeneralUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LadderAnimation extends BasicAnimation implements PoseOverwrite {
   private final Set<Class<? extends Block>> ladderLikeBlocks = new HashSet<>(Arrays.asList(LadderBlock.class, TrapDoorBlock.class));
   private final BodyPart[] parts = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, BodyPart.BODY, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG, BodyPart.HEAD};
   private final BodyPart[] partsSneakingRight = new BodyPart[]{BodyPart.RIGHT_ARM, BodyPart.BODY, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG, BodyPart.HEAD};
   private final BodyPart[] partsSneakingLeft = new BodyPart[]{BodyPart.LEFT_ARM, BodyPart.BODY, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG, BodyPart.HEAD};

   @Override
   public boolean isEnabled() {
      return NEABaseMod.config.enableLadderAnimation;
   }

   @Override
   public boolean isValid(AbstractClientPlayer entity, PlayerData data) {
      if (entity.onClimbable() && !NMSWrapper.onGround(entity) && entity.getLastClimbablePos().isPresent()) {
         for (Class<? extends Block> blocktype : this.ladderLikeBlocks) {
            if (blocktype.isAssignableFrom(GeneralUtil.getWorld().getBlockState((BlockPos)entity.getLastClimbablePos().get()).getBlock().getClass())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public BodyPart[] getBodyParts(AbstractClientPlayer entity, PlayerData data) {
      if (entity.isCrouching() && entity.getDeltaMovement().y == -0.0784000015258789) {
         return entity.getMainArm() == HumanoidArm.RIGHT ? this.partsSneakingLeft : this.partsSneakingRight;
      } else {
         return this.parts;
      }
   }

   @Override
   public int getPriority(AbstractClientPlayer entity, PlayerData data) {
      return 1400;
   }

   @Override
   public void apply(AbstractClientPlayer entity, PlayerData data, PlayerModel model, BodyPart part, float delta, float tickCounter) {
      if (part != BodyPart.HEAD) {
         if (part == BodyPart.BODY && NEABaseMod.config.enableRotateToLadder) {
            BlockState blockState = entity.getInBlockState();
            if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
               Direction dir = (Direction)blockState.getValue(HorizontalDirectionalBlock.FACING);
               data.setDisableBodyRotation(true);
               switch (dir) {
                  case NORTH:
                     entity.setYBodyRot(0.0F);
                     entity.yBodyRotO = 0.0F;
                     break;
                  case EAST:
                     entity.setYBodyRot(90.0F);
                     entity.yBodyRotO = 90.0F;
                     break;
                  case SOUTH:
                     entity.setYBodyRot(180.0F);
                     entity.yBodyRotO = 180.0F;
                     break;
                  case WEST:
                     entity.setYBodyRot(270.0F);
                     entity.yBodyRotO = 270.0F;
               }

               AnimationUtil.minMaxHeadRotation(entity, model);
            }
         } else if (part != BodyPart.LEFT_LEG && part != BodyPart.RIGHT_LEG) {
            float rotation = -Mth.cos((float)(entity.getY() * NEABaseMod.config.ladderAnimationArmSpeed));
            rotation *= NEABaseMod.config.ladderAnimationAmplifier;
            if (part == BodyPart.LEFT_ARM) {
               rotation *= -1.0F;
            }

            AnimationUtil.applyTransforms(model, part, -NEABaseMod.config.ladderAnimationArmHeight - rotation, -0.2F, 0.3F);
         } else {
            float rotation = -Mth.cos((float)(entity.getY() * NEABaseMod.config.ladderAnimationArmSpeed));
            rotation *= NEABaseMod.config.ladderAnimationAmplifier;
            if (part == BodyPart.LEFT_LEG) {
               rotation *= -1.0F;
            }

            AnimationUtil.applyTransforms(model, part, -1.0F - rotation, -0.2F, 0.3F);
         }
      }
   }

   @Override
   public void updateState(AbstractClientPlayer entity, PlayerData data, PlayerModel playerModel) {
      if (entity.isCrouching() && this.isValid(entity, data)) {
         data.setPoseOverwrite(entity.getPose());
         entity.setPose(Pose.STANDING);
         playerModel.crouching = false;
      }
   }
}
