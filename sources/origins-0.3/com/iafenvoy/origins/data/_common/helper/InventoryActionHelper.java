package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.wrapper.ContainerWrapper;
import com.iafenvoy.origins.util.wrapper.Mutable;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface InventoryActionHelper extends InventoryConditionHelper {
   EntityAction entityAction();

   ItemAction itemAction();

   default void modifyInventory(Entity entity, Function<ItemStack, Integer> processor, int limit) {
      if (limit <= 0) {
         limit = 2147483647;
      }

      Set<Integer> slots = this.deduplicateSlots(entity);
      int counter = 0;
      ContainerWrapper container = this.getWrappedContainer(entity);

      for (int slot : slots) {
         SlotAccess access = container.get(slot);
         if (access != SlotAccess.NULL) {
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition().test(entity.level(), stack)) {
               this.entityAction().execute(entity);
               int amount = processor.apply(stack);
               int i = 0;

               while (true) {
                  if (i < amount) {
                     Mutable.Stack newStack = Mutable.stack(stack);
                     this.itemAction().execute(entity.level(), entity, newStack.toSlotAccess());
                     access.set(newStack.get());
                     if (++counter < limit) {
                        i++;
                        continue;
                     }
                  }

                  if (counter >= limit) {
                     return;
                  }
                  break;
               }
            }
         }
      }
   }

   default void replaceInventory(ItemStack replacementStack, Entity entity, boolean mergeComponent) {
      Set<Integer> slots = this.deduplicateSlots(entity);
      ContainerWrapper container = this.getWrappedContainer(entity);

      for (Integer slot : slots) {
         SlotAccess access = container.get(slot);
         if (access != SlotAccess.NULL) {
            ItemStack stack = access.get();
            if (this.itemCondition().test(entity.level(), stack)) {
               this.entityAction().execute(entity);
               Mutable.Stack newStack = Mutable.stack(replacementStack.copy());
               if (mergeComponent && !newStack.get().isComponentsPatchEmpty()) {
                  newStack.get().applyComponents(replacementStack.getComponents());
               }

               this.itemAction().execute(entity.level(), entity, newStack.toSlotAccess());
               access.set(newStack.get());
            }
         }
      }
   }

   default void dropInventory(boolean throwRandomly, boolean retainOwnership, Entity entity, int amount) {
      Set<Integer> slots = this.deduplicateSlots(entity);
      ContainerWrapper container = this.getWrappedContainer(entity);

      for (Integer slot : slots) {
         SlotAccess access = container.get(slot);
         if (access != SlotAccess.NULL) {
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition().test(entity.level(), stack)) {
               this.entityAction().execute(entity);
               Mutable.Stack newStack = Mutable.stack(stack.copy());
               this.itemAction().execute(entity.level(), entity, newStack.toSlotAccess());
               if (amount != 0) {
                  int newAmount = amount > 0 ? amount * -1 : amount;
                  newStack.set(newStack.get().split(newAmount));
                  access.set(newStack.get());
               } else {
                  access.set(ItemStack.EMPTY);
               }

               this.throwItem(entity, newStack.get(), throwRandomly, retainOwnership);
            }
         }
      }
   }

   default void throwItem(Entity thrower, ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
      if (!stack.isEmpty()) {
         if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide) {
            playerEntity.swing(InteractionHand.MAIN_HAND);
         }

         double yOffset = thrower.getEyeY() - 0.3;
         ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), stack);
         itemEntity.setPickUpDelay(40);
         Random random = new Random();
         if (retainOwnership) {
            itemEntity.setThrower(thrower);
         }

         if (throwRandomly) {
            double f = random.nextFloat() * 0.5F;
            double g = random.nextFloat() * 2.0F * 3.141592653589793;
            itemEntity.setDeltaMovement(-Math.sin(g) * f, 0.2, Math.cos(g) * f);
         } else {
            double f = 0.30000001192092896;
            double g = Math.sin(Math.toRadians(thrower.getXRot()));
            double h = Math.cos(Math.toRadians(thrower.getXRot()));
            double i = Math.sin(Math.toRadians(thrower.getYRot()));
            double j = Math.cos(Math.toRadians(thrower.getYRot()));
            double k = random.nextFloat() * 3.141592653589793;
            double l = 0.02F * random.nextFloat();
            itemEntity.setDeltaMovement(
               -i * h * f + Math.cos(k) * l, -g * f + (1.0F + random.nextFloat() - random.nextFloat()) * 0.1, j * h * f + Math.sin(k) * l
            );
         }

         thrower.level().addFreshEntity(itemEntity);
      }
   }
}
