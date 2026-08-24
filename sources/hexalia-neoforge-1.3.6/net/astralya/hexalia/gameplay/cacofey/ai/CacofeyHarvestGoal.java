package net.astralya.hexalia.gameplay.cacofey.ai;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.entity.custom.CacofeyEntity;
import net.astralya.hexalia.entity.custom.CacofeyMode;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class CacofeyHarvestGoal extends Goal {
   private static final double ARRIVE_DISTANCE = 2.0;
   private static final float TRAVEL_SPEED = 1.2F;
   private static final int SCAN_COOLDOWN = 40;
   private final CacofeyEntity cacofey;
   private CacofeyHarvestGoal.Phase phase = CacofeyHarvestGoal.Phase.IDLE;
   private BlockPos cropPos = null;
   private int scanTimer = 0;

   public CacofeyHarvestGoal(CacofeyEntity cacofey) {
      this.cacofey = cacofey;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.cacofey.isTame()) {
         return false;
      } else if (this.cacofey.getMode() != CacofeyMode.WANDER) {
         return false;
      } else if (this.cacofey.getAnchorPos() == null) {
         return false;
      } else if (this.phase != CacofeyHarvestGoal.Phase.IDLE) {
         return true;
      } else if (this.scanTimer > 0) {
         this.scanTimer--;
         return false;
      } else {
         return this.findMatureCrop();
      }
   }

   public boolean canContinueToUse() {
      if (this.cacofey.getMode() != CacofeyMode.WANDER) {
         return false;
      } else {
         return this.cacofey.getAnchorPos() == null ? false : this.phase != CacofeyHarvestGoal.Phase.IDLE;
      }
   }

   public void start() {
      this.phase = CacofeyHarvestGoal.Phase.MOVING_TO_CROP;
      this.navigateToCrop();
   }

   public void stop() {
      this.cacofey.getNavigation().stop();
      this.phase = CacofeyHarvestGoal.Phase.IDLE;
      this.cropPos = null;
      this.scanTimer = 40;
   }

   public void tick() {
      switch (this.phase) {
         case MOVING_TO_CROP:
            this.tickMoveToCrop();
            break;
         case HARVESTING:
            this.tickHarvest();
            break;
         case MOVING_TO_CONTAINER:
            this.tickMoveToContainer();
            break;
         case DEPOSITING:
            this.tickDeposit();
      }
   }

   private void tickMoveToCrop() {
      if (this.cropPos != null && isMatureCrop(this.cacofey.level().getBlockState(this.cropPos))) {
         this.cacofey.getLookControl().setLookAt(this.cropPos.getX() + 0.5, this.cropPos.getY() + 0.5, this.cropPos.getZ() + 0.5, 30.0F, 30.0F);
         if (this.distanceToCrop() <= 2.0) {
            this.cacofey.getNavigation().stop();
            this.phase = CacofeyHarvestGoal.Phase.HARVESTING;
         }
      } else {
         this.phase = CacofeyHarvestGoal.Phase.IDLE;
      }
   }

   private void tickHarvest() {
      if (this.cacofey.level() instanceof ServerLevel serverLevel) {
         BlockState state = serverLevel.getBlockState(this.cropPos);
         if (!isMatureCrop(state)) {
            this.phase = CacofeyHarvestGoal.Phase.IDLE;
         } else {
            List<ItemStack> drops = Block.getDrops(state, serverLevel, this.cropPos, serverLevel.getBlockEntity(this.cropPos), null, ItemStack.EMPTY);
            ItemStack harvest = drops.stream().filter(s -> !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
            if (!harvest.isEmpty()) {
               this.cacofey.setHeldItem(harvest.copyWithCount(1));
            }

            resetCropAge(serverLevel, this.cropPos, state);
            spawnHarvestParticles(serverLevel, this.cropPos);
            this.phase = CacofeyHarvestGoal.Phase.MOVING_TO_CONTAINER;
            this.navigateToContainer();
         }
      }
   }

   private void tickMoveToContainer() {
      BlockPos anchor = this.cacofey.getAnchorPos();
      this.cacofey.getLookControl().setLookAt(this.cropPos.getX() + 0.5, this.cropPos.getY() + 0.5, this.cropPos.getZ() + 0.5, 30.0F, 30.0F);
      if (this.cacofey.distanceToSqr(Vec3.atCenterOf(anchor.above())) <= 4.0) {
         this.cacofey.getNavigation().stop();
         this.phase = CacofeyHarvestGoal.Phase.DEPOSITING;
      }
   }

   private void tickDeposit() {
      if (this.cacofey.level() instanceof ServerLevel) {
         BlockPos anchor = this.cacofey.getAnchorPos();
         if (this.cacofey.level().getBlockEntity(anchor) instanceof Container container) {
            ItemStack held = this.cacofey.getHeldItem();
            if (!held.isEmpty()) {
               insertIntoContainer(container, held);
            }
         }

         this.cacofey.setHeldItem(ItemStack.EMPTY);
         this.phase = CacofeyHarvestGoal.Phase.IDLE;
         this.scanTimer = 40;
      }
   }

   private boolean findMatureCrop() {
      BlockPos anchor = this.cacofey.getAnchorPos();
      int radius = HexaliaConfig.cacofeyHarvestRadius();

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            if (dx * dx + dz * dz <= radius * radius) {
               for (int dy = -3; dy <= 3; dy++) {
                  BlockPos candidate = anchor.offset(dx, dy, dz);
                  if (isMatureCrop(this.cacofey.level().getBlockState(candidate))) {
                     this.cropPos = candidate;
                     return true;
                  }
               }
            }
         }
      }

      this.scanTimer = 40;
      return false;
   }

   private static boolean isMatureCrop(BlockState state) {
      for (Property<?> property : state.getProperties()) {
         if (property.getName().equals("age") && property instanceof IntegerProperty intProp) {
            int maxAge = Collections.<Integer>max(intProp.getPossibleValues());
            return (Integer)state.getValue(intProp) == maxAge;
         }
      }

      return false;
   }

   private static void resetCropAge(ServerLevel level, BlockPos pos, BlockState state) {
      for (Property<?> property : state.getProperties()) {
         if (property.getName().equals("age") && property instanceof IntegerProperty intProp) {
            level.setBlock(pos, (BlockState)state.setValue(intProp, 0), 3);
            return;
         }
      }
   }

   private static void insertIntoContainer(Container container, ItemStack stack) {
      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack slot = container.getItem(i);
         if (slot.isEmpty()) {
            container.setItem(i, stack.copy());
            container.setChanged();
            return;
         }

         if (ItemStack.isSameItemSameComponents(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
            slot.grow(stack.getCount());
            container.setChanged();
            return;
         }
      }
   }

   private void navigateToCrop() {
      if (this.cropPos != null) {
         this.cacofey.getNavigation().moveTo(this.cropPos.getX() + 0.5, this.cropPos.getY() + 1.5, this.cropPos.getZ() + 0.5, 1.2000000476837158);
      }
   }

   private void navigateToContainer() {
      BlockPos anchor = this.cacofey.getAnchorPos();
      if (anchor != null) {
         this.cacofey.getNavigation().moveTo(anchor.getX() + 0.5, anchor.getY() + 1.5, anchor.getZ() + 0.5, 1.2000000476837158);
      }
   }

   private double distanceToCrop() {
      return this.cacofey.position().distanceTo(Vec3.atCenterOf(this.cropPos.above()));
   }

   private static void spawnHarvestParticles(ServerLevel level, BlockPos pos) {
      level.sendParticles(
         (SimpleParticleType)ModParticleTypes.CACOFEY_DUST.get(), pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.012
      );
      level.sendParticles(
         (SimpleParticleType)ModParticleTypes.CACOFEY_DUST_HELD.get(), pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 6, 0.2, 0.15, 0.2, 0.018
      );
   }

   private static enum Phase {
      IDLE,
      MOVING_TO_CROP,
      HARVESTING,
      MOVING_TO_CONTAINER,
      DEPOSITING;
   }
}
