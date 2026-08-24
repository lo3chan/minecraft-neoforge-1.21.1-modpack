package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RustyProperty extends AbstractTimerProperty {
   public static final float RUST_DURATION = 72000.0F;

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (damageSource.is(DamageTypes.LIGHTNING_BOLT)) {
         this.resetStartTimestamp(weapon);
      }
   }

   @Override
   public int modifyDurabilityConsumed(
      ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random
   ) {
      this.setData(stack, this.getData(stack) - 100L);
      return random.nextFloat() <= this.getRustAmount(stack) * 0.75F ? resultingAmount * 2 : resultingAmount;
   }

   protected void setStartTimestamp(ItemStack stack) {
      long dayTime = AlchemancyServerData.getGlobalTimer();
      long timestamp = this.getData(stack);
      if (dayTime < timestamp || timestamp == 0L) {
         this.setData(stack, AlchemancyServerData.getGlobalTimer());
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide()) {
         this.setStartTimestamp(stack);
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (!itemEntity.level().isClientSide()) {
         this.setStartTimestamp(stack);
      }
   }

   public float getMiningSpeedMultiplier(ItemStack stack) {
      return InfusedPropertiesHelper.hasProperty(stack, this.asHolder()) ? this.getRustAmount(stack) + 1.0F : 1.0F;
   }

   public float getRustAmount(ItemStack stack) {
      return Mth.clamp((float)this.getElapsedTime(stack) / 72000.0F, 0.0F, 1.0F);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.lerp(this.getRustAmount(stack), 15170646, 5878418);
   }
}
