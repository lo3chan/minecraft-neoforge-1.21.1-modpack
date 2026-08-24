package net.joefoxe.hexerei.data.recipes;

import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CutCandleRecipe extends CustomRecipe {
   public CutCandleRecipe(CraftingBookCategory cBc) {
      super(cBc);
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean matches(CraftingInput pInv, Level pLevel) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() instanceof SwordItem) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!itemstack1.is((Item)ModItems.CANDLE.get())) {
                  return false;
               }

               i++;
            }
         }
      }

      return !itemstack.isEmpty() && i == 1;
   }

   public ItemStack assemble(CraftingInput pInv, Provider registryAccess) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for (int j = 0; j < pInv.size(); j++) {
         ItemStack itemstack1 = pInv.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() instanceof SwordItem) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }
            } else {
               if (!itemstack1.is((Item)ModItems.CANDLE.get())) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
               i++;
            }
         }
      }

      if (!itemstack.isEmpty() && i >= 1 && CandleItem.getHeight(itemstack) > 1) {
         ItemStack itemstack2 = itemstack.copy();
         itemstack2.setCount(1);
         CandleItem.setHeight(itemstack2, CandleItem.getHeight(itemstack2) - 1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack itemstack = pInv.getItem(i);
         if (itemstack.hasCraftingRemainingItem()) {
            nonnulllist.set(i, itemstack.getCraftingRemainingItem());
         } else if (itemstack.getItem() instanceof SwordItem) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.CUT_CANDLE_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth >= 3 && pHeight >= 3;
   }
}
