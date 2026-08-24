package net.joefoxe.hexerei.data.recipes;

import java.util.UUID;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.WhistleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WhistleBindRecipe extends CustomRecipe {
   public WhistleBindRecipe(CraftingBookCategory cBc) {
      super(cBc);
   }

   public boolean isSpecial() {
      return true;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput p_44004_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_44004_.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack item = p_44004_.getItem(i);
         if (item.hasCraftingRemainingItem()) {
            nonnulllist.set(i, item.getCraftingRemainingItem());
         }

         if (item.getItem() instanceof BroomItem broomItem) {
            nonnulllist.set(i, item.copy());
         }
      }

      return nonnulllist;
   }

   public boolean matches(CraftingInput inventory, Level world) {
      int whistle = 0;
      int other = 0;
      int broom = 0;
      ItemStack whistleItem = null;

      for (int i = 0; i < inventory.size(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof WhistleItem) {
               whistle++;
               whistleItem = stack;
            } else if (stack.getItem() instanceof BroomItem) {
               broom++;
            } else {
               other++;
            }

            if (other > 1 || whistle > 1 || broom > 1) {
               return false;
            }
         }
      }

      return whistle == 1
         && other == 0
         && (broom == 1 || ((CustomData)whistleItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().contains("broomUUID"));
   }

   public ItemStack assemble(CraftingInput inventory, Provider pRegistryAccess) {
      ItemStack whistleItem = ItemStack.EMPTY;
      UUID broomUUID = null;

      for (int i = 0; i < inventory.size(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof WhistleItem) {
               whistleItem = stack.copy();
               whistleItem.setCount(1);
            }

            if (stack.getItem() instanceof BroomItem) {
               broomUUID = BroomItem.getUUID(stack);
            }
         }
      }

      CompoundTag tag = ((CustomData)whistleItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (broomUUID != null) {
         tag.putUUID("broomUUID", broomUUID);
      } else {
         tag.remove("broomUUID");
      }

      if (!tag.isEmpty()) {
         whistleItem.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      return whistleItem;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width >= 2 && height >= 1;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.WHISTLE_BIND_SERIALIZER.get();
   }
}
