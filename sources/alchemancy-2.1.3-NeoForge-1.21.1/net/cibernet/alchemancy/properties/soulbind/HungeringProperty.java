package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.HollowProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class HungeringProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user.tickCount % 10 == 0 && !user.level().isClientSide()) {
         if (!this.eat(user, stack, false)
            && (slot.isArmor() || InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY))
            && user instanceof Player player) {
            Inventory inventory = player.getInventory();
            if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW)) {
               ItemStack storedStack = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
               if (!storedStack.isEmpty()) {
                  this.eat(user, storedStack, true);
                  ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, storedStack);
               }
            } else {
               int i = 0;

               while (i < inventory.items.size() && !this.eat(user, inventory.getItem(i), true)) {
                  i++;
               }
            }
         }
      }
   }

   private boolean eat(LivingEntity user, ItemStack stack, boolean eatEfficiently) {
      FoodData foodData = user instanceof Player player ? player.getFoodData() : null;
      FoodProperties food = stack.getFoodProperties(user);
      if (foodData != null
         && (
            food == null
               || !food.canAlwaysEat()
                  && (!eatEfficiently ? !foodData.needsFood() : foodData.getFoodLevel() > 0 && foodData.getFoodLevel() > 20 - food.nutrition())
         )) {
         return false;
      } else {
         stack.finishUsingItem(user.level(), user);
         return true;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 10828644;
   }
}
