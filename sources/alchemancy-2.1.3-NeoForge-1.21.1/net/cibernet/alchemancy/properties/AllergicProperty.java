package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;

public class AllergicProperty extends AbstractTimerProperty {
   private boolean processing = false;

   @Override
   public void onMobEffectAdded(ItemStack stack, EquipmentSlot slot, LivingEntity user, Added event) {
      if (!this.processing && this.isAvailable(stack) && event.getOldEffectInstance() == null) {
         this.processing = true;
         this.resetStartTimestamp(stack);
         user.level()
            .playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)AlchemancySoundEvents.ALLERGIC.value(), SoundSource.PLAYERS, 0.5F, 1.0F);
         activateByEntity(user, user, stack);
         this.processing = false;
      }
   }

   public boolean isAvailable(ItemStack stack) {
      return !this.hasRecordedTimestamp(stack) || this.getElapsedTime(stack) > 5L || this.getElapsedTime(stack) <= 0L;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 7196940;
   }
}
