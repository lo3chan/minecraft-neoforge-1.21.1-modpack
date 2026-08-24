package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class KineticRechargeProperty extends AbstractTimerProperty {
   private static final int BASE_TIME = 60;

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide() && canRepair(stack)) {
         int vel = Math.max(10, 60 - (int)Math.ceil(user.getKnownMovement().length() * 100.0));
         if (vel >= 60) {
            this.removeData(stack);
         } else {
            if (!this.hasRecordedTimestamp(stack)) {
               this.resetStartTimestamp(stack);
            } else if (this.getElapsedTime(stack) >= vel) {
               repairItem(stack, 1);
               this.resetStartTimestamp(stack);
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16746496;
   }
}
