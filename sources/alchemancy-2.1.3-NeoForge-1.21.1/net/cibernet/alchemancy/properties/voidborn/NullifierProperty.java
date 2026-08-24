package net.cibernet.alchemancy.properties.voidborn;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.BucketingProperty;
import net.cibernet.alchemancy.properties.CapturingProperty;
import net.cibernet.alchemancy.properties.EncapsulatingProperty;
import net.cibernet.alchemancy.properties.HollowProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NullifierProperty extends Property {
   @Override
   public void onStackedOverItem(
      ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get()
         && clickAction == ClickAction.SECONDARY
         && !InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW)
         && !InfusedPropertiesHelper.hasProperty(stackedOnItem, AlchemancyProperties.HOLLOW)) {
         stackedOnItem.setCount(0);
         isCancelled.set(true);
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get() && clickAction == ClickAction.SECONDARY && !InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW)) {
         carriedItem.setCount(0);
         isCancelled.set(true);
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      this.nullify(stack);
   }

   protected void nullify(ItemStack stack) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.RESTOCKER)) {
         ItemStack storedItem = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
         if (storedItem.getCount() > 1) {
            storedItem.setCount(1);
            ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, storedItem);
         }
      } else {
         ((HollowProperty)AlchemancyProperties.HOLLOW.get()).removeData(stack);
      }

      ((EncapsulatingProperty)AlchemancyProperties.ENCAPSULATING.get()).removeData(stack);
      ((BucketingProperty)AlchemancyProperties.BUCKETING.get()).removeData(stack);
      ((CapturingProperty)AlchemancyProperties.CAPTURING.get()).removeData(stack);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11419362;
   }
}
