package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.network.S2CRotatePlayerPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RotatingProperty extends Property {
   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      return rotateBlock(blockSource.level(), blockSource.pos().relative(direction), direction.getOpposite())
         ? InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS
         : currentResult;
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      Level level = event.getLevel();
      BlockPos pos = event.getPos();
      Direction face = event.getFace();
      if (face != null) {
         if (rotateBlock(level, pos, face)) {
            event.setCancellationResult(ItemInteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   private static boolean rotateBlock(Level level, BlockPos pos, Direction face) {
      BlockState state = level.getBlockState(pos);
      BlockState newState = null;
      if (state.hasProperty(BlockStateProperties.FACING)
         && state.getValue(BlockStateProperties.FACING) != ((Direction)state.getValue(BlockStateProperties.FACING)).getClockWise(face.getAxis())) {
         newState = (BlockState)state.setValue(
            BlockStateProperties.FACING, ((Direction)state.getValue(BlockStateProperties.FACING)).getClockWise(face.getAxis())
         );
      } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
         newState = (BlockState)state.setValue(
            BlockStateProperties.HORIZONTAL_FACING, ((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).getClockWise()
         );
      } else if (state.hasProperty(BlockStateProperties.FACING_HOPPER)) {
         newState = (BlockState)state.setValue(
            BlockStateProperties.FACING_HOPPER, ((Direction)state.getValue(BlockStateProperties.FACING_HOPPER)).getClockWise()
         );
      } else if (state.hasProperty(BlockStateProperties.AXIS)) {
         newState = (BlockState)state.setValue(BlockStateProperties.AXIS, rotateAxis((Axis)state.getValue(BlockStateProperties.AXIS), face.getAxis()));
      } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
         newState = (BlockState)state.setValue(
            BlockStateProperties.HORIZONTAL_AXIS, rotateAxis((Axis)state.getValue(BlockStateProperties.HORIZONTAL_AXIS), Axis.Y)
         );
      } else if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
         newState = (BlockState)state.setValue(
            BlockStateProperties.ROTATION_16, ((Integer)state.getValue(BlockStateProperties.ROTATION_16) + 1) % RotationSegment.getMaxSegmentIndex()
         );
      }

      if (newState != null && newState.canSurvive(level, pos)) {
         level.setBlock(pos, newState, 3);
         return true;
      } else {
         return false;
      }
   }

   private static Axis rotateAxis(Axis axis, Axis from) {
      if (from == Axis.X) {
         return switch (axis) {
            case X -> Axis.X;
            case Y -> Axis.Z;
            case Z -> Axis.Y;
            default -> throw new MatchException(null, null);
         };
      } else if (from == Axis.Y) {
         return switch (axis) {
            case X -> Axis.Z;
            case Y -> Axis.Y;
            case Z -> Axis.X;
            default -> throw new MatchException(null, null);
         };
      } else {
         return switch (axis) {
            case X -> Axis.Y;
            case Y -> Axis.X;
            case Z -> Axis.Z;
            default -> throw new MatchException(null, null);
         };
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      Player user = event.getEntity();
      double distance = user.entityInteractionRange();
      EntityHitResult hit = ProjectileUtil.getEntityHitResult(
         user,
         user.getEyePosition(),
         user.getEyePosition().add(user.getLookAngle().scale(distance)),
         CommonUtils.boundingBoxAroundPoint(user.getEyePosition(), (float)distance),
         e -> !e.isSpectator(),
         distance
      );
      if (hit != null) {
         this.rotateEntity(hit.getEntity());
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      this.rotateEntity(event.getTarget());
      event.setCancellationResult(InteractionResult.SUCCESS);
      event.setCanceled(true);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (target != source || target.level().isClientSide()) {
         this.rotateEntity(target);
      }
   }

   private void rotateEntity(Entity target) {
      if (target instanceof ServerPlayer player) {
         PacketDistributor.sendToPlayer(player, new S2CRotatePlayerPayload(45.0F), new CustomPacketPayload[0]);
      } else {
         float prevRot = target.getYRot();
         target.setYRot(prevRot - 45.0F);
         target.setYHeadRot(prevRot);
         target.setOnGround(false);
         target.hurtMarked = true;
         if (target instanceof LivingEntity living) {
            target.setYBodyRot(living.yBodyRot - 45.0F);
            living.setNoActionTime(60);
         }

         if (target instanceof PathfinderMob pathfinderMob) {
            pathfinderMob.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
            pathfinderMob.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16775047;
   }
}
