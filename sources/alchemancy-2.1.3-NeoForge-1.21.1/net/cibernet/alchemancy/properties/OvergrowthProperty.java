package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.ItemEntityAccessor;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class OvergrowthProperty extends Property {
   private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (carriedItem.is(Items.SHEARS) || InfusedPropertiesHelper.hasProperty(carriedItem, AlchemancyProperties.SHEARING)) {
         player.playSound(SoundEvents.VINE_BREAK);
         stackedOnSlot.set(InfusedPropertiesHelper.removeProperty(stack, this.asHolder()));
         isCancelled.set(true);
      }
   }

   @Override
   public int onItemRepaired(ItemStack stack, int amount, int originalAmount) {
      return amount * 3;
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!user.level().isClientSide() && !(user.getRandom().nextFloat() >= 0.05F) && ARMOR_SLOTS.contains(slot)) {
         boolean fullyInfected = true;
         ArrayList<EquipmentSlot> shuffled = new ArrayList<>(ARMOR_SLOTS);
         Collections.shuffle(shuffled);

         for (EquipmentSlot slotToCheck : shuffled) {
            ItemStack stackToCheck = user.getItemBySlot(slotToCheck);
            if (!stackToCheck.isEmpty() && !InfusedPropertiesHelper.hasProperty(stackToCheck, this.asHolder())) {
               fullyInfected = false;
               this.infect(user, slot);
               if (user.getRandom().nextBoolean()) {
                  return;
               }
            }
         }

         if (fullyInfected && user instanceof Player player) {
            this.infect(player, player.getRandom().nextInt(player.getInventory().items.size()));
         }
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!user.level().isClientSide() && !(user.getRandom().nextFloat() >= 0.005F) && user instanceof Player player && inventorySlot < 36) {
         RandomSource random = player.getRandom();
         if (random.nextBoolean() && Math.floorDiv(inventorySlot, 9) == Math.floorDiv(inventorySlot - 1, 9)) {
            this.infect(player, inventorySlot - 1);
         }

         if (random.nextBoolean() && Math.floorDiv(inventorySlot, 9) == Math.floorDiv(inventorySlot + 1, 9)) {
            this.infect(player, inventorySlot + 1);
         }

         if (random.nextBoolean() && inventorySlot > 8) {
            this.infect(player, (inventorySlot + 9) % 36);
         }

         if (random.nextBoolean() && (inventorySlot < 9 || inventorySlot > 17)) {
            this.infect(player, Mth.positiveModulo(inventorySlot - 9, 36));
         }
      }
   }

   protected void infect(LivingEntity target, EquipmentSlot slot) {
      ItemStack stack = this.infect(target.level(), target.getItemBySlot(slot));
      if (stack.getCount() > stack.getMaxStackSize()) {
         ItemStack split = stack.split(stack.getCount() - stack.getMaxStackSize());
         split.setCount(1);
         if (target instanceof Player player) {
            if (!player.addItem(split)) {
               player.drop(split, true, true);
            }
         } else {
            ItemEntity itemEntity = HollowProperty.nonPlayerDrop(target, split, true, true);
            if (itemEntity != null) {
               ((ItemEntityAccessor)itemEntity).setAge(itemEntity.lifespan - 60);
            }
         }
      }

      target.setItemSlot(slot, stack);
   }

   protected void infect(Player player, int slot) {
      int originalAmount = player.getInventory().getItem(slot).getCount();
      ItemStack stack = this.infect(player.level(), player.getInventory().getItem(slot));
      int addedAmount = stack.getCount() - originalAmount;
      if (!stack.isEmpty()) {
         ItemStack split;
         if (addedAmount > 0) {
            split = stack.split(addedAmount);
            InfusedPropertiesHelper.getInfusedProperties(split)
               .stream()
               .filter(propertyHolder -> !propertyHolder.is(AlchemancyTags.Properties.CLONED_BY_OVERGROWTH))
               .forEach(propertyHolder -> InfusedPropertiesHelper.removeProperty(split, (Holder<Property>)propertyHolder));
            if (ItemStack.isSameItemSameComponents(stack, split)) {
               int count = stack.getCount() + split.getCount();
               stack.setCount(Math.min(stack.getMaxStackSize(), count));
            }

            split.setCount(split.getCount() / originalAmount);
         } else if (stack.getCount() > stack.getMaxStackSize()) {
            split = stack.split(stack.getCount() - stack.getMaxStackSize());
            split.setCount(split.getCount() / originalAmount);
         } else {
            split = ItemStack.EMPTY;
         }

         if (!split.isEmpty() && !player.addItem(split)) {
            ItemEntity itemEntity = player.drop(split, true, true);
            if (itemEntity != null) {
               ((ItemEntityAccessor)itemEntity).setAge(60);
            }
         }

         player.getInventory().setItem(slot, stack);
      }
   }

   protected ItemStack infect(Level level, ItemStack stack) {
      InfusedPropertiesHelper.addProperty(stack, this.asHolder());
      return ForgeRecipeGrid.resolveInteractions(stack, level);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 58368;
   }
}
