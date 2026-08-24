package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;

public class AbsorbingProperty extends Property {
   private static final HashMap<Predicate<ItemStack>, AbsorbingProperty.AbsorbingBehavior> BEHAVIORS = new HashMap<Predicate<ItemStack>, AbsorbingProperty.AbsorbingBehavior>() {
      {
         this.put(
            AbsorbingProperty.hasProperty(AlchemancyProperties.CLAY_MOLD),
            new AbsorbingProperty.AbsorbingBehavior(
               (stack, consumeStack) -> consumeStack.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY), (stack, consumeStack, player, slot) -> {
                  ItemStack storedItem = ClayMoldProperty.repair(((ClayMoldProperty)AlchemancyProperties.CLAY_MOLD.get()).getData(stack));
                  if (player.getItemBySlot(slot) == stack && stack.getCount() <= 1) {
                     player.setItemSlot(slot, storedItem);
                  } else if (!player.addItem(storedItem)) {
                     player.drop(storedItem, true);
                  }

                  ClayMoldProperty.playRepairEffects(player);
                  stack.shrink(1);
                  consumeStack.shrink(1);
               }
            )
         );
         this.put(
            AbsorbingProperty.hasProperty(AlchemancyProperties.WAXED, stack -> ((WaxedProperty)AlchemancyProperties.WAXED.get()).getData(stack) <= 1),
            new AbsorbingProperty.AbsorbingBehavior(
               (stack, consumeStack) -> consumeStack.is(AlchemancyTags.Items.RESTORES_WAXED), (target, toConsume, user, slot) -> {
                  ((WaxedProperty)AlchemancyProperties.WAXED.get()).removeData(target);
                  WaxedProperty.playRestoreSound(user);
                  toConsume.shrink(1);
               }
            )
         );
         this.put(
            AbsorbingProperty.hasProperty(AlchemancyProperties.SMELTING, stack -> ((AutosmeltProperty)AlchemancyProperties.SMELTING.get()).getData(stack) <= 0),
            new AbsorbingProperty.AbsorbingBehavior(
               (stack, consumeStack) -> consumeStack.getBurnTime(RecipeType.SMELTING) > 0, (target, toConsume, user, slot) -> {
                  int fuel = toConsume.getBurnTime(RecipeType.SMELTING) / 200;
                  if (toConsume.hasCraftingRemainingItem()) {
                     ItemStack remainder = toConsume.getCraftingRemainingItem();
                     if (!user.addItem(remainder)) {
                        user.drop(remainder, true);
                     }
                  }

                  toConsume.shrink(1);
                  ((AutosmeltProperty)AlchemancyProperties.SMELTING.get()).setData(target, fuel);
                  AutosmeltProperty.playRefuelSound(user);
               }
            )
         );
      }
   };
   private static final AbsorbingProperty.AbsorbingBehavior REPAIR = new AbsorbingProperty.AbsorbingBehavior(
      (stack, consumeStack) -> stack.getItem().isValidRepairItem(stack, consumeStack), (stack, consumeStack, player, slot) -> {
         repairItem(stack, stack.getMaxDamage() / 4);
         consumeStack.shrink(1);
      }
   );

   public static void registerAbsorbingBehavior(Predicate<ItemStack> baseItemPredicate, AbsorbingProperty.AbsorbingBehavior behavior) {
      BEHAVIORS.put(baseItemPredicate, behavior);
   }

   @Override
   public void onStackedOverMe(
      ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (shouldRepair(stack) && stack.getItem().isValidRepairItem(stack, otherStack)) {
         repairItem(stack, stack.getMaxDamage() / 4);
         otherStack.shrink(1);
         isCancelled.set(true);
      }
   }

   @Override
   public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, Pre event) {
      itemToPickUp.setNoPickUpDelay();
   }

   public static boolean scanInventoryAndConsume(ItemStack stack, Player player, EquipmentSlot slot) {
      ArrayList<AbsorbingProperty.AbsorbingBehavior> behaviors = new ArrayList<>();
      if (shouldRepair(stack)) {
         behaviors.add(REPAIR);
      }

      BEHAVIORS.entrySet().stream().filter(v -> v.getKey().test(stack)).map(Entry::getValue).forEach(behaviors::add);

      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack otherStack = player.getInventory().getItem(i);
         ItemStack repairStack = otherStack;
         ItemStack storedStack = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(otherStack);
         if (!storedStack.isEmpty()) {
            repairStack = storedStack;
         }

         for (AbsorbingProperty.AbsorbingBehavior behavior : behaviors) {
            if (stack != repairStack && behavior.predicate.test(stack, repairStack)) {
               behavior.function.apply(stack, repairStack, player, slot);
               if (storedStack == repairStack) {
                  ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(otherStack, storedStack);
               }

               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user instanceof Player player) {
         scanInventoryAndConsume(stack, player, slot);
      }

      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BUCKETING)
         && ((BucketingProperty)AlchemancyProperties.BUCKETING.get()).isEmpty(stack)) {
         Level level = user.level();

         for (int i = 0; i <= 1; i++) {
            BlockPos hitPos = user.blockPosition().above(i);
            BlockState hitState = level.getBlockState(hitPos);
            if (hitState.getBlock() instanceof BucketPickup bucketPickup
               && bucketPickup.pickupBlock(user instanceof Player player ? player : null, level, hitPos, hitState).getItem() instanceof BucketItem bucketItem) {
               bucketPickup.getPickupSound(hitState).ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
               level.gameEvent(user, GameEvent.FLUID_PICKUP, hitPos);
               ((BucketingProperty)AlchemancyProperties.BUCKETING.get()).setData(stack, bucketItem.content);
               return;
            }
         }
      }
   }

   public static boolean shouldRepair(ItemStack stack) {
      return stack.isRepairable() && canRepair(stack, stack.getMaxDamage() / 4);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 15394909;
   }

   private static Predicate<ItemStack> hasProperty(Holder<Property> propertyHolder) {
      return hasProperty(propertyHolder, stack -> true);
   }

   private static Predicate<ItemStack> hasProperty(Holder<Property> propertyHolder, Predicate<ItemStack> extraCheck) {
      return extraCheck.and(stack -> InfusedPropertiesHelper.hasProperty(stack, propertyHolder));
   }

   public record AbsorbingBehavior(BiPredicate<ItemStack, ItemStack> predicate, AbsorbingProperty.AbsorbingConsumer function) {
   }

   public interface AbsorbingConsumer {
      void apply(ItemStack var1, ItemStack var2, Player var3, EquipmentSlot var4);
   }
}
