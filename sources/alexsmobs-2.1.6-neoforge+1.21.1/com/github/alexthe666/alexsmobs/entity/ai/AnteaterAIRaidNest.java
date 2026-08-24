package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAnthill;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AnteaterAIRaidNest extends MoveToBlockGoal {
   public static final ResourceLocation ANTEATER_REWARD = AMCompat.rl("alexsmobs", "gameplay/anteater_reward");
   private final EntityAnteater anteater;
   private int idleAtHiveTime = 0;
   private boolean isAboveDestinationAnteater;
   private boolean shootTongue;
   private int maxEatingTime = 0;

   public AnteaterAIRaidNest(EntityAnteater anteater) {
      super(anteater, 1.0, 32, 8);
      this.anteater = anteater;
   }

   private static List<ItemStack> getItemStacks(EntityAnteater anteater) {
      LootTable loottable = AMCompat.lootTable(anteater.level().getServer(), ANTEATER_REWARD);
      return loottable.getRandomItems(
         new Builder((ServerLevel)anteater.level()).withParameter(LootContextParams.THIS_ENTITY, anteater).create(LootContextParamSets.PIGLIN_BARTER)
      );
   }

   private void dropDigItems() {
      List<ItemStack> lootList = getItemStacks(this.anteater);
      if (lootList.size() > 0) {
         for (ItemStack stack : lootList) {
            ItemEntity e = AMCompat.spawnAtLocation(this.anteater, stack.copy());
            e.hasImpulse = true;
            e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
         }
      }
   }

   public boolean canUse() {
      return !this.anteater.isBaby() && super.canUse() && this.anteater.eatAntCooldown <= 0;
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.anteater.eatAntCooldown <= 0;
   }

   public void start() {
      super.start();
      this.maxEatingTime = 150 + this.anteater.getRandom().nextInt(200);
   }

   public void stop() {
      super.stop();
      this.idleAtHiveTime = 0;
      this.maxEatingTime = 150 + this.anteater.getRandom().nextInt(200);
      this.anteater.setLeaning(false);
      this.anteater.resetAntCooldown();
   }

   public double acceptedDistance() {
      return 1.2;
   }

   public void tick() {
      super.tick();
      BlockPos blockpos = this.getMoveToTarget();
      if (!this.isWithinXZDist(blockpos, this.mob.position(), this.acceptedDistance())) {
         this.isAboveDestinationAnteater = false;
         this.tryTicks++;
         if (this.shouldRecalculatePath()) {
            this.mob.getNavigation().moveTo(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5, this.speedModifier);
         }
      } else {
         this.isAboveDestinationAnteater = true;
         this.tryTicks--;
      }

      if (this.isReachedTarget()) {
         this.anteater.lookAt(Anchor.EYES, new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY() - 1, this.blockPos.getZ() + 0.5));
         if (this.idleAtHiveTime >= 20 && this.idleAtHiveTime % 20 == 0) {
            this.shootTongue = this.anteater.getRandom().nextInt(2) == 0;
            if (this.shootTongue) {
               this.eatHive();
            } else {
               this.breakHiveEffect();
            }
         }

         this.idleAtHiveTime++;
         if (this.shootTongue && this.anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.anteater.setLeaning(false);
            this.anteater.setAnimation(EntityAnteater.ANIMATION_TOUNGE_IDLE);
         } else if (this.anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.anteater.setLeaning(true);
            this.anteater.setAnimation(this.anteater.getRandom().nextBoolean() ? EntityAnteater.ANIMATION_SLASH_L : EntityAnteater.ANIMATION_SLASH_R);
         }

         if (this.idleAtHiveTime > this.maxEatingTime) {
            this.stop();
         }
      }
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.distSqr(AMBlockPos.fromCoords(positionVec.x(), blockpos.getY(), positionVec.z())) < distance * distance;
   }

   protected boolean isReachedTarget() {
      return this.isAboveDestinationAnteater;
   }

   private void breakHiveEffect() {
      if (AMPlatform.mobGriefing(this.anteater.level(), this.anteater)) {
         BlockState blockstate = this.anteater.level().getBlockState(this.blockPos);
         if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
            if (this.anteater.level().getBlockEntity(this.blockPos) instanceof TileEntityLeafcutterAnthill) {
               TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill)this.anteater.level().getBlockEntity(this.blockPos);
               anthill.angerAntsBecauseAnteater(this.anteater, blockstate, BeeReleaseStatus.EMERGENCY);
               this.anteater.level().destroyBlock(this.blockPos, false);
               if (blockstate.getBlock() instanceof BlockLeafcutterAnthill) {
                  this.anteater.level().setBlockAndUpdate(this.blockPos, blockstate);
               }

               this.dropDigItems();
            }
         } else if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            this.anteater.level().destroyBlock(this.blockPos, false);
            this.anteater.level().setBlockAndUpdate(this.blockPos, blockstate);
         }
      }
   }

   private void eatHive() {
      if (AMPlatform.mobGriefing(this.anteater.level(), this.anteater)) {
         BlockState blockstate = this.anteater.level().getBlockState(this.blockPos);
         if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
            if (this.anteater.level().getBlockEntity(this.blockPos) instanceof TileEntityLeafcutterAnthill) {
               RandomSource rand = this.anteater.getRandom();
               TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill)this.anteater.level().getBlockEntity(this.blockPos);
               anthill.angerAntsBecauseAnteater(this.anteater, blockstate, BeeReleaseStatus.EMERGENCY);
               this.anteater.level().updateNeighbourForOutputSignal(this.blockPos, blockstate.getBlock());
               if (!anthill.hasNoAnts()) {
                  BlockState state = anthill.shrinkFungus();
                  if (state != null && state.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()) && (Integer)state.getValue(BlockLeafcutterAntChamber.FUNGUS) >= 5
                     )
                   {
                     ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.GONGYLIDIA.get());
                     ItemEntity itementity = new ItemEntity(
                        this.anteater.level(),
                        this.blockPos.getX() + rand.nextFloat(),
                        this.blockPos.getY() + rand.nextFloat(),
                        this.blockPos.getZ() + rand.nextFloat(),
                        stack
                     );
                     itementity.setDefaultPickUpDelay();
                     this.anteater.level().addFreshEntity(itementity);
                  }

                  this.anteater.setAntOnTongue(true);
               }
            }
         } else if (blockstate.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            this.anteater.level().destroyBlock(this.blockPos, false);
            if ((Integer)blockstate.getValue(BlockLeafcutterAntChamber.FUNGUS) >= 5) {
               RandomSource rand = this.anteater.getRandom();
               ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.GONGYLIDIA.get());
               ItemEntity itementity = new ItemEntity(
                  this.anteater.level(),
                  this.blockPos.getX() + rand.nextFloat(),
                  this.blockPos.getY() + rand.nextFloat(),
                  this.blockPos.getZ() + rand.nextFloat(),
                  stack
               );
               itementity.setDefaultPickUpDelay();
               this.anteater.level().addFreshEntity(itementity);
            }

            this.anteater.level().setBlockAndUpdate(this.blockPos, Blocks.COARSE_DIRT.defaultBlockState());
            this.anteater.setAntOnTongue(true);
         }

         double d0 = 15.0;

         for (EntityLeafcutterAnt leafcutter : this.anteater
            .level()
            .getEntitiesOfClass(
               EntityLeafcutterAnt.class,
               new AABB(
                  this.blockPos.getX() - d0,
                  this.blockPos.getY() - d0,
                  this.blockPos.getZ() - d0,
                  this.blockPos.getX() + d0,
                  this.blockPos.getY() + d0,
                  this.blockPos.getZ() + d0
               )
            )) {
            leafcutter.setRemainingPersistentAngerTime(100);
            leafcutter.setTarget(this.anteater);
            leafcutter.setStayOutOfHiveCountdown(400);
         }
      }
   }

   protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())
         || worldIn.getBlockState(pos).is(AMBlockRegistry.LEAFCUTTER_ANTHILL.get())
            && worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill
            && this.isValidAnthill(pos, (TileEntityLeafcutterAnthill)worldIn.getBlockEntity(pos));
   }

   private boolean isValidAnthill(BlockPos pos, TileEntityLeafcutterAnthill blockEntity) {
      return blockEntity.hasAtleastThisManyAnts(2);
   }
}
