package io.github.razordevs.deep_aether.entity.living.quail;

import com.aetherteam.aether.entity.passive.AetherAnimal;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public abstract class SittingAetherAnimal extends AetherAnimal {
   private static final EntityDataAccessor<Boolean> DATA_SITTING_ID = SynchedEntityData.defineId(SittingAetherAnimal.class, EntityDataSerializers.BOOLEAN);
   private int sitCounter;

   protected SittingAetherAnimal(EntityType<? extends Animal> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_SITTING_ID, false);
   }

   public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Sitting")) {
         this.setSitting(tag.getBoolean("Sitting"));
      }
   }

   public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Sitting", this.isSitting());
   }

   public boolean isSitting() {
      return (Boolean)this.entityData.get(DATA_SITTING_ID);
   }

   public void setSitting(boolean isSitting) {
      this.entityData.set(DATA_SITTING_ID, isSitting);
   }

   public void tryToSit() {
      if (!this.isInWaterOrBubble() && this.onGround() && this.isEffectiveAi()) {
         this.setZza(0.0F);
         this.getNavigation().stop();
         this.setSitting(true);
      }
   }

   public void tick() {
      super.tick();
      if (this.isEffectiveAi() && this.sitCounter > 0 && ++this.sitCounter > 20) {
         this.sitCounter = 0;
         this.setSitting(false);
      }
   }

   static class RandomSittingGoal extends Goal {
      private int cooldown;
      private final SittingAetherAnimal mob;

      public RandomSittingGoal(SittingAetherAnimal animal) {
         this.mob = animal;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canUse() {
         return this.cooldown <= this.mob.tickCount && !this.mob.isInWater();
      }

      public boolean canContinueToUse() {
         return !this.mob.isInWater() && this.mob.random.nextInt(reducedTickDelay(1000)) != 1 ? this.mob.random.nextInt(reducedTickDelay(2500)) != 1 : false;
      }

      public void tick() {
         if (!this.mob.isSitting()) {
            this.mob.tryToSit();
         }
      }

      public void start() {
         this.mob.tryToSit();
         this.cooldown = 0;
      }

      public void stop() {
         int i = this.mob.random.nextInt(150) + 10;
         this.cooldown = this.mob.tickCount + i * 20;
         this.mob.setSitting(false);
      }
   }
}
