package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AnimalAILootChests extends MoveToBlockGoal {
   private final Animal entity;
   private final ILootsChests chestLooter;
   private boolean hasOpenedChest = false;

   public AnimalAILootChests(Animal entity, int range) {
      super(entity, 1.0, range);
      this.entity = entity;
      this.chestLooter = (ILootsChests)entity;
   }

   public boolean isChestRaidable(LevelReader world, BlockPos pos) {
      if (world.getBlockState(pos).getBlock() instanceof BaseEntityBlock) {
         Block block = world.getBlockState(pos).getBlock();
         boolean listed = false;
         BlockEntity entity = world.getBlockEntity(pos);
         if (entity instanceof Container inventory) {
            try {
               if (!inventory.isEmpty() && this.chestLooter.isLootable(inventory)) {
                  return true;
               }
            } catch (Exception var8) {
               AlexsMobs.LOGGER.warn("Alex's Mobs stopped a " + entity.getClass().getSimpleName() + " from causing a crash during access");
               var8.printStackTrace();
            }
         }
      }

      return false;
   }

   public boolean canUse() {
      if (this.entity instanceof TamableAnimal && ((TamableAnimal)this.entity).isTame()) {
         return false;
      } else if (!AMConfig.raccoonsStealFromChests) {
         return false;
      } else if (!this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
         return false;
      } else {
         return this.nextStartTick <= 0 && !AMPlatform.mobGriefing(this.entity.level(), this.entity) ? false : super.canUse();
      }
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
   }

   public boolean hasLineOfSightChest() {
      HitResult raytraceresult = this.entity
         .level()
         .clip(
            new ClipContext(
               this.entity.getEyePosition(1.0F),
               new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5),
               net.minecraft.world.level.ClipContext.Block.COLLIDER,
               Fluid.NONE,
               this.entity
            )
         );
      if (!(raytraceresult instanceof BlockHitResult blockRayTraceResult)) {
         return true;
      } else {
         BlockPos pos = blockRayTraceResult.getBlockPos();
         return pos.equals(this.blockPos)
            || this.entity.level().isEmptyBlock(pos)
            || this.entity.level().getBlockEntity(pos) == this.entity.level().getBlockEntity(this.blockPos);
      }
   }

   public ItemStack getFoodFromInventory(Container inventory, RandomSource random) {
      List<ItemStack> items = new ArrayList<>();

      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (this.chestLooter.shouldLootItem(stack)) {
            items.add(stack);
         }
      }

      if (items.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         return items.size() == 1 ? items.get(0) : items.get(random.nextInt(items.size() - 1));
      }
   }

   public void tick() {
      super.tick();
      if (this.blockPos != null) {
         if (this.entity.level().getBlockEntity(this.blockPos) instanceof Container feeder) {
            double distance = this.entity.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F);
            if (this.hasLineOfSightChest()) {
               if (this.isReachedTarget() && distance <= 3.0) {
                  this.toggleChest(feeder, false);
                  ItemStack stack = this.getFoodFromInventory(feeder, this.entity.level().getRandom());
                  if (stack == ItemStack.EMPTY) {
                     this.stop();
                  } else {
                     ItemStack duplicate = stack.copy();
                     duplicate.setCount(1);
                     if (!this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.entity.level().isClientSide()) {
                        AMCompat.spawnAtLocation(this.entity, this.entity.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
                     }

                     this.entity.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
                     if (this.entity instanceof EntityRaccoon) {
                        ((EntityRaccoon)this.entity).lookForWaterBeforeEatingTimer = 10;
                     }

                     stack.shrink(1);
                     this.stop();
                  }
               } else if (distance < 5.0 && !this.hasOpenedChest) {
                  this.hasOpenedChest = true;
                  this.toggleChest(feeder, true);
               }
            }
         }
      }
   }

   public void stop() {
      super.stop();
      if (this.blockPos != null) {
         BlockEntity te = this.entity.level().getBlockEntity(this.blockPos);
         if (te instanceof Container) {
            this.toggleChest((Container)te, false);
         }
      }

      this.blockPos = BlockPos.ZERO;
      this.hasOpenedChest = false;
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return pos != null && this.isChestRaidable(worldIn, pos);
   }

   public void toggleChest(Container te, boolean open) {
      if (te instanceof ChestBlockEntity chest) {
         if (open) {
            this.entity.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 1);
         } else {
            this.entity.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 0);
         }

         this.entity.level().updateNeighborsAt(this.blockPos, chest.getBlockState().getBlock());
         this.entity.level().updateNeighborsAt(this.blockPos.below(), chest.getBlockState().getBlock());
      }
   }
}
