package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EarlyAssemblingProperty extends Property {
   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (source instanceof Player player) {
         craftItem(player, stack, null);
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY) {
         isCancelled.set(true);
         ItemStack craftedItem = craftItem(player, stack, carriedItem);
         if (craftedItem != null) {
            if (carriedItem.isEmpty()) {
               carriedSlot.set(craftedItem);
            } else {
               carriedItem.setCount(carriedItem.getCount() + craftedItem.getCount());
            }
         }
      }
   }

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player player, InteractionHand hand, BlockHitResult hitResult) {
      return craftItem(player, root.getItem(), null) != null ? ItemInteractionResult.SUCCESS : null;
   }

   @Nullable
   private static ItemStack craftItem(Player player, ItemStack toCraft, @Nullable ItemStack constraintTo) {
      HashMap<CraftingRecipe, NonNullList<Integer>> acceptedRecipes = new HashMap<>();
      Level level = player.level();
      RegistryAccess registryAccess = level.registryAccess();
      List<RecipeHolder<CraftingRecipe>> availableRecipes = AssembleProperty.getRecipesMatchingOutput(toCraft, level);
      Iterator selected = availableRecipes.iterator();

      while (true) {
         RecipeHolder<CraftingRecipe> craftingRecipe;
         NonNullList<Integer> acceptedSlots;
         ItemStack result;
         do {
            if (!selected.hasNext()) {
               if (acceptedRecipes.isEmpty()) {
                  return null;
               }

               CraftingRecipe selectedx = acceptedRecipes.keySet().stream().toList().get(player.getRandom().nextInt(acceptedRecipes.size()));
               NonNullList<Integer> selectedList = acceptedRecipes.get(selectedx);
               ItemStack resultItem = selectedx.getResultItem(registryAccess).copy();
               Inventory inventory = player.getInventory();
               if (constraintTo == null) {
                  int count = resultItem.getCount();
                  ArrayList<Integer> checkedSlots = new ArrayList<>();

                  while (count > 0) {
                     int slot = getSlotWithRemainingSpace(player.getInventory(), resultItem, checkedSlots);
                     if (slot == -1) {
                        return null;
                     }

                     checkedSlots.add(slot);
                     ItemStack stackInSlot = inventory.getItem(slot);
                     count -= stackInSlot.getMaxStackSize() - stackInSlot.getCount();
                  }

                  inventory.add(resultItem);
               }

               for (int slotToShrink = 0; slotToShrink < player.getInventory().getContainerSize(); slotToShrink++) {
                  if ((Integer)selectedList.get(slotToShrink) > 0
                     && !((HollowProperty)AlchemancyProperties.HOLLOW.get())
                        .shrinkContents(player.getInventory().getItem(slotToShrink), (Integer)selectedList.get(slotToShrink))) {
                     inventory.removeItem(slotToShrink, (Integer)selectedList.get(slotToShrink));
                  }
               }

               return resultItem;
            }

            craftingRecipe = (RecipeHolder<CraftingRecipe>)selected.next();
            acceptedSlots = NonNullList.withSize(player.getInventory().getContainerSize(), 0);
            if (constraintTo == null) {
               break;
            }

            result = ((CraftingRecipe)craftingRecipe.value()).getResultItem(registryAccess);
         } while (
            !constraintTo.isEmpty()
               && result.getCount() + constraintTo.getCount() > constraintTo.getMaxStackSize()
               && ItemStack.isSameItemSameComponents(result, constraintTo)
         );

         boolean accepted = true;

         for (Ingredient ingredient : ((CraftingRecipe)craftingRecipe.value()).getIngredients()) {
            if (!ingredient.isEmpty()) {
               boolean hasIngredient = false;

               for (int i = 0; i < acceptedSlots.size(); i++) {
                  ItemStack inventoryStack = player.getInventory().getItem(i);
                  ItemStack storedItem = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(inventoryStack);
                  if (!storedItem.isEmpty()) {
                     inventoryStack = storedItem;
                  }

                  if (toCraft != inventoryStack
                     && !InfusedPropertiesHelper.hasProperty(inventoryStack, AlchemancyProperties.ASSEMBLING)
                     && (Integer)acceptedSlots.get(i) < inventoryStack.getCount()
                     && ingredient.test(inventoryStack)) {
                     hasIngredient = true;
                     acceptedSlots.set(i, (Integer)acceptedSlots.get(i) + 1);
                     break;
                  }
               }

               if (!hasIngredient) {
                  accepted = false;
                  break;
               }
            }
         }

         if (accepted) {
            acceptedRecipes.put((CraftingRecipe)craftingRecipe.value(), acceptedSlots);
         }
      }
   }

   private static int getSlotWithRemainingSpace(Inventory inventory, ItemStack stack, List<Integer> alreadyChecked) {
      if (!alreadyChecked.contains(inventory.selected) && hasRemainingSpaceForItem(inventory, inventory.getItem(inventory.selected), stack)) {
         return inventory.selected;
      } else if (!alreadyChecked.contains(40) && hasRemainingSpaceForItem(inventory, inventory.getItem(40), stack)) {
         return 40;
      } else {
         for (int i = 0; i < inventory.items.size(); i++) {
            if (!alreadyChecked.contains(i) && hasRemainingSpaceForItem(inventory, (ItemStack)inventory.items.get(i), stack)) {
               return i;
            }
         }

         return -1;
      }
   }

   private static boolean hasRemainingSpaceForItem(Inventory inventory, ItemStack destination, ItemStack origin) {
      return destination.isEmpty()
         || ItemStack.isSameItemSameComponents(destination, origin)
            && destination.isStackable()
            && destination.getCount() < inventory.getMaxStackSize(destination);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 2877951;
   }
}
