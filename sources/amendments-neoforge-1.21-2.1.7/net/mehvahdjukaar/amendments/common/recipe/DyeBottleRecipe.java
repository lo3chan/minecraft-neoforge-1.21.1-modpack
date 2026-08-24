package net.mehvahdjukaar.amendments.common.recipe;

import net.mehvahdjukaar.amendments.common.item.DyeBottleItem;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DyeBottleRecipe extends CustomRecipe {
   public DyeBottleRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput input, Level level) {
      ItemStack dyeBottle = ItemStack.EMPTY;
      ItemStack otherItem = ItemStack.EMPTY;

      for (ItemStack itemstack : input.items()) {
         if (!itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            if (item == ModRegistry.DYE_BOTTLE_ITEM.get()) {
               if (!dyeBottle.isEmpty()) {
                  return false;
               }

               dyeBottle = itemstack;
            } else {
               if (!otherItem.isEmpty()) {
                  return false;
               }

               otherItem = itemstack;
            }
         }
      }

      return !dyeBottle.isEmpty() && !otherItem.isEmpty()
         ? otherItem.is(ItemTags.DYEABLE) || BlocksColorAPI.changeColor(otherItem.getItem(), DyeBottleItem.getClosestDye(dyeBottle)) != null
         : false;
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      ItemStack leather = ItemStack.EMPTY;
      ItemStack dyeBottle = ItemStack.EMPTY;

      for (ItemStack itemstack : input.items()) {
         if (!itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            if (item == ModRegistry.DYE_BOTTLE_ITEM.get()) {
               dyeBottle = itemstack;
            } else {
               leather = itemstack;
            }
         }
      }

      ItemStack result;
      if (leather.is(ItemTags.DYEABLE)) {
         result = leather.copy();
         DyedItemColor colorComponent = (DyedItemColor)leather.get(DataComponents.DYED_COLOR);
         if (colorComponent != null) {
            int mixedColor = DyeBottleItem.mixColor(((DyedItemColor)dyeBottle.get(DataComponents.DYED_COLOR)).rgb(), colorComponent.rgb(), 1, 1);
            result.set(DataComponents.DYED_COLOR, new DyedItemColor(mixedColor, true));
         } else {
            result.set(DataComponents.DYED_COLOR, new DyedItemColor(((DyedItemColor)dyeBottle.get(DataComponents.DYED_COLOR)).rgb(), true));
         }
      } else {
         DyeColor dye = DyeBottleItem.getClosestDye(dyeBottle);
         Item changed = BlocksColorAPI.changeColor(leather.getItem(), dye);
         if (changed == null) {
            return ItemStack.EMPTY;
         }

         result = changed.getDefaultInstance();
         result.applyComponents(leather.getComponents());
      }

      return result;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height > 2;
   }

   public String getGroup() {
      return "dye_bottle";
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRegistry.DYE_BOTTLE_RECIPE.get();
   }
}
