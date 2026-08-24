package net.cibernet.alchemancy.properties;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CluelessProperty extends Property {
   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      AtomicBoolean performed = new AtomicBoolean(false);
      InfusedPropertiesHelper.forEachProperty(
         stack,
         propertyHolder -> {
            if (propertyHolder.value() instanceof IDataHolder<?> dataHolder
               && dataHolder.cluelessCanReset()
               && !Objects.equals(dataHolder.getData(stack), dataHolder.getDefaultData())) {
               dataHolder.removeData(stack);
               performed.set(true);
            }
         }
      );
      if (performed.get()) {
         user.playSound((SoundEvent)AlchemancySoundEvents.CLUELESS.value());
         if (InfusedPropertiesHelper.hasInfusedProperty(stack, this.asHolder())) {
            InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 9502814;
   }
}
