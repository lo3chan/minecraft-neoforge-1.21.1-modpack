package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class GliderProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide() && slot == EquipmentSlot.CHEST && user.isFallFlying()) {
         int nextFlightTick = user.getFallFlyingTicks() + 1;
         if (nextFlightTick % 10 == 0) {
            if (nextFlightTick % 20 == 0) {
               this.damageItem(user, stack, slot, 1);
            }

            user.gameEvent(GameEvent.ELYTRA_GLIDE);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9408435;
   }
}
