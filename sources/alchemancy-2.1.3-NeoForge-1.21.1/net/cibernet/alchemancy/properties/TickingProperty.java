package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TickingProperty extends AbstractTimerProperty {
   private static final long TIMER_DURATION = 100L;

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!this.hasRecordedTimestamp(stack)) {
         this.resetStartTimestamp(stack);
      } else {
         boolean tickingSound = !InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.MUFFLED);
         long elapsedTime = this.getElapsedTime(stack);
         long duration = this.getTimerDuration(stack);
         if (elapsedTime < duration) {
            if (tickingSound && elapsedTime % Math.ceil(20.0F * ((float)duration / 100.0F)) == 0.0) {
               user.playSound((SoundEvent)AlchemancySoundEvents.TICKING.value(), 0.2F, 0.8F);
            }
         } else if (elapsedTime == duration) {
            AtomicBoolean activate = new AtomicBoolean(true);
            InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
               if (activate.get() && !((Property)propertyHolder.value()).shouldActivate(user.level(), user, user, stack)) {
                  activate.set(false);
               }
            });
            if (activate.get()) {
               if (tickingSound) {
                  user.playSound((SoundEvent)AlchemancySoundEvents.TICKING.value(), 0.2F, 1.2F);
               }

               InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
                  if (!propertyHolder.equals(this.asHolder())) {
                     ((Property)propertyHolder.value()).onActivation(user, user, stack);
                  }
               });
               if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE)) {
                  this.resetStartTimestamp(stack);
               }
            }
         }
      }
   }

   public long getTimerDuration(ItemStack stack) {
      long result = 100L;
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SWIFT)) {
         result /= 2L;
      }

      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SLUGGISH)) {
         result *= 2L;
      }

      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LAZY)) {
         result *= 2L;
      }

      return result;
   }

   @Override
   public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
      this.resetStartTimestamp(stack);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      this.resetStartTimestamp(stack);
   }

   @Override
   public int getColor(ItemStack stack) {
      long timeLeft = this.getTimerDuration(stack) - this.getElapsedTime(stack);
      return timeLeft > 0L ? this.getTickingColor(1.0F) : 4261120;
   }

   private int getTickingColor(float speed) {
      return System.currentTimeMillis() / (int)(speed * 1000.0F) % 2L == 0L ? 16711680 : 16438858;
   }
}
