package io.github.razordevs.deep_aether.entity.living;

import io.github.razordevs.deep_aether.init.DAItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class AerglowFish extends AbstractSchoolingFish {
   public AerglowFish(EntityType<? extends AerglowFish> entityType, Level level) {
      super(entityType, level);
   }

   public int getMaxSchoolSize() {
      return 5;
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack((ItemLike)DAItems.AERGLOW_FISH_BUCKET.get());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SALMON_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_29795_) {
      return SoundEvents.SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.SALMON_FLOP;
   }
}
