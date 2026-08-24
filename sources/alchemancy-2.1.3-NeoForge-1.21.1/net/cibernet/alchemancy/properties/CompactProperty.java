package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CompactProperty extends Property {
   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get()
         && stack.getCount() < stack.getMaxStackSize()
         && ItemStack.isSameItemSameComponents(stack, InfusedPropertiesHelper.addProperty(carriedItem.copy(), this.asHolder()))) {
         InfusedPropertiesHelper.addProperty(carriedItem, this.asHolder());
      }
   }

   @Override
   public void onStackedOverItem(
      ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (!isCancelled.get()
         && stack.getCount() < stack.getMaxStackSize()
         && ItemStack.isSameItemSameComponents(stack, InfusedPropertiesHelper.addProperty(stackedOnItem.copy(), this.asHolder()))) {
         InfusedPropertiesHelper.addProperty(stackedOnItem, this.asHolder());
      }
   }

   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      if (dataType == DataComponents.MAX_STACK_SIZE && !stack.is(AlchemancyTags.Items.DISABLES_COMPACT) && data instanceof Integer maxStackSize) {
         for (DataComponentType<?> dataComponentType : stack.getComponents().keySet()) {
            if (BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.DISABLES_COMPACT)) {
               return data;
            }
         }

         return Math.min(96, maxStackSize * 4);
      } else {
         return data;
      }
   }

   @Override
   public int getPriority() {
      return -2147483648;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11919615;
   }
}
