package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RestoreClayMoldCraftingRecipe extends CustomRecipe {
   public RestoreClayMoldCraftingRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput input, Level level) {
      if (input.ingredientCount() != 2) {
         return false;
      } else {
         boolean hasClay = false;

         for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
               if (!hasClay && stack.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY)) {
                  hasClay = true;
               } else if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLAY_MOLD)) {
                  return false;
               }
            }
         }

         return hasClay;
      }
   }

   public ItemStack assemble(CraftingInput input, Provider registries) {
      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLAY_MOLD)) {
            return ClayMoldProperty.repair(((ClayMoldProperty)AlchemancyProperties.CLAY_MOLD.get()).getData(stack));
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.RESTORE_CLAY_MOLD_CRAFTING.get();
   }
}
