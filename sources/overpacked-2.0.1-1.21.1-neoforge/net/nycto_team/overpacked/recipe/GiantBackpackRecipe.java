package net.nycto_team.overpacked.recipe;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.nycto_team.overpacked.item.GiantBackpackItem;
import net.nycto_team.overpacked.registry.ModRecipes;

public class GiantBackpackRecipe extends CustomRecipe {
   public GiantBackpackRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput crafting_input, Level level) {
      boolean has_backpack = false;
      boolean has_ingredient = false;

      for (ItemStack stack : crafting_input.items()) {
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof GiantBackpackItem) {
               if (has_backpack) {
                  return false;
               }

               has_backpack = true;
            } else if (stack.getItem() instanceof DyeItem) {
               if (has_ingredient) {
                  return false;
               }

               has_ingredient = true;
            }
         }
      }

      return has_backpack && has_ingredient;
   }

   public ItemStack assemble(CraftingInput crafting_input, Provider provider) {
      ItemStack stack = ItemStack.EMPTY;
      DyeColor color = DyeColor.WHITE;

      for (ItemStack i_stack : crafting_input.items()) {
         if (!i_stack.isEmpty()) {
            if (i_stack.getItem() instanceof GiantBackpackItem) {
               stack = i_stack;
            } else if (DyeColor.getColor(i_stack) != null) {
               color = DyeColor.getColor(i_stack);
            }
         }
      }

      return stack.transmuteCopy(GiantBackpackItem.get_colored_stack(color.getId()).getItem(), 1);
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipes.backpack_coloring.get();
   }
}
