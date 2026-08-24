package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RestockerProperty extends Property {
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
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user instanceof Player player) {
         boolean scattershot = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SCATTERSHOT);
         DeferredHolder<Property, ? extends HollowProperty> propertyToStock = scattershot ? AlchemancyProperties.SCATTERSHOT : AlchemancyProperties.HOLLOW;
         if (slot.isArmor() && !scattershot && !InfusedPropertiesHelper.hasProperty(stack, propertyToStock)) {
            slot = EquipmentSlot.MAINHAND;
            stack = user.getItemBySlot(slot);
         }

         Inventory inventory = player.getInventory();
         ItemStack storedItem = ((HollowProperty)propertyToStock.get()).getData(stack);
         ItemStack toCompare = storedItem.isEmpty() ? stack : storedItem;
         if (toCompare.getCount() < toCompare.getMaxStackSize()) {
            for (int i = 0; i < inventory.getContainerSize() && toCompare.getCount() < toCompare.getMaxStackSize(); i++) {
               ItemStack other = inventory.getItem(i);
               if (toCompare != other
                  && ItemStack.isSameItemSameComponents(
                     InfusedPropertiesHelper.removeProperty(
                        InfusedPropertiesHelper.removeProperty(toCompare.copy(), this.asHolder()), AlchemancyProperties.COMPACT
                     ),
                     InfusedPropertiesHelper.removeProperty(InfusedPropertiesHelper.removeProperty(other.copy(), this.asHolder()), AlchemancyProperties.COMPACT)
                  )) {
                  toCompare.setCount(toCompare.getCount() + other.split(toCompare.getMaxStackSize() - toCompare.getCount()).getCount());
               }
            }

            if (!storedItem.isEmpty()) {
               ((HollowProperty)propertyToStock.get()).setData(stack, toCompare);
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 65365;
   }
}
