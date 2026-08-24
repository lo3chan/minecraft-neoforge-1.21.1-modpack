package net.mehvahdjukaar.amendments.common.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CauldronRecipeUtils {
   @Nullable
   public static FluidAndItemsCraftResult craftMultiple(Level level, boolean boiling, int tankCapacity, SoftFluidStack currentFluid, List<ItemStack> inputItems) {
      boolean success = false;
      CompactItemSet craftedItems = new CompactItemSet();

      FluidAndItemCraftResult currentResult;
      do {
         currentResult = craft(level, boiling, tankCapacity, currentFluid, inputItems);
         if (currentResult != null) {
            success = true;
            currentFluid = currentResult.resultFluid();
            craftedItems.add(currentResult.craftedItem());
         }
      } while (currentResult != null);

      return !success ? null : FluidAndItemsCraftResult.of(craftedItems.toList(), currentFluid);
   }

   @Nullable
   public static FluidAndItemCraftResult craft(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluidStack, List<ItemStack> items) {
      if (fluidStack.isEmpty()) {
         return null;
      } else {
         FluidAndItemCraftResult crafted = craftFluidSpecial(level, boiling, tankCapacity, fluidStack, items);
         if (crafted != null) {
            return crafted;
         } else {
            if (boiling || !fluidStack.is(MLBuiltinSoftFluids.WATER)) {
               crafted = craftItemSingle(level, boiling, tankCapacity, fluidStack, items);
               if (crafted != null) {
                  return crafted;
               }

               crafted = craftItemSplit(level, boiling, tankCapacity, fluidStack, items);
               if (crafted != null) {
                  return crafted;
               }

               if (items.size() == 1) {
                  return craftItemSurround(level, boiling, tankCapacity, fluidStack, (ItemStack)items.getFirst());
               }
            }

            return null;
         }
      }
   }

   private static List<ItemStack> expandItems(List<ItemStack> items) {
      List<ItemStack> itemList = new ArrayList<>();

      for (ItemStack stack : items) {
         int count = stack.getCount();

         for (int i = 0; i < count; i++) {
            itemList.add(stack.copyWithCount(1));
         }
      }

      return itemList;
   }

   @Nullable
   private static FluidAndItemCraftResult craftItemSingle(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluid, Collection<ItemStack> item) {
      CauldronCraftingContainer container = CauldronCraftingContainer.of(boiling, tankCapacity, fluid, item);
      FluidAndItemCraftResult result = container.craftWithCraftingRecipes(level);
      if (result != null) {
         for (ItemStack i : item) {
            if (!i.isEmpty()) {
               i.shrink(1);
            }
         }

         return result;
      } else {
         return null;
      }
   }

   @Nullable
   private static FluidAndItemCraftResult craftItemSplit(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluid, Collection<ItemStack> item) {
      List<ItemStack> expandedItems = expandItems(new ArrayList<>(item));
      CauldronCraftingContainer container = CauldronCraftingContainer.of(boiling, tankCapacity, fluid, expandedItems);
      FluidAndItemCraftResult result = container.craftWithCraftingRecipes(level);
      if (result == null) {
         return null;
      } else {
         for (ItemStack i : expandedItems) {
            if (!i.isEmpty()) {
               for (ItemStack orig : item) {
                  if (ItemStack.isSameItemSameComponents(i, orig)) {
                     orig.shrink(1);
                     break;
                  }
               }
            }
         }

         return result;
      }
   }

   @Nullable
   private static FluidAndItemCraftResult craftItemSurround(Level level, boolean boiling, int tankCapacity, SoftFluidStack fluid, ItemStack item) {
      if (item.getCount() < 8) {
         return null;
      } else {
         CauldronCraftingContainer container = CauldronCraftingContainer.surround8(boiling, tankCapacity, fluid, item);
         FluidAndItemCraftResult result8x = container.craftWithCraftingRecipes(level);
         if (result8x != null) {
            item.shrink(8);
            return result8x;
         } else {
            return null;
         }
      }
   }

   @Nullable
   public static FluidAndItemCraftResult craftFluidSpecial(
      Level level, boolean boiling, int tankCapacity, SoftFluidStack softFluidStack, Collection<ItemStack> items
   ) {
      CauldronCraftingContainer container = CauldronCraftingContainer.of(boiling, tankCapacity, softFluidStack, items);
      FluidAndItemCraftResult crafted = container.craftWithCauldronRecipes(level);
      if (crafted != null) {
         for (ItemStack i : items) {
            if (!i.isEmpty()) {
               i.shrink(1);
            }
         }

         return crafted;
      } else {
         return null;
      }
   }
}
